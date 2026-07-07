package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.ConfirmarPagoDTO;
import com.idasta.jetstore.dto.PagarRequestDTO;
import com.idasta.jetstore.model.*;
import com.idasta.jetstore.repository.CarritoRepo;
import com.idasta.jetstore.repository.PagoRepo;
import com.idasta.jetstore.repository.VentaRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

    @Mock
    private CarritoRepo carritoRepo;
    @Mock
    private PagoRepo pagoRepo;
    @Mock
    private VentaRepo ventaRepo;

    @InjectMocks
    private PagoServiceImpl service;

    private Carrito crearCarritoConItems() {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("testuser");

        Libro libro = new Libro();
        libro.setId(1L);
        libro.setTitulo("Libro Test");
        libro.setPrecio(BigDecimal.TEN);

        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.agregarOActualizarItem(libro, 2);
        return carrito;
    }

    @Test
    void procesar_creaPagoPendiente() {
        Carrito carrito = crearCarritoConItems();
        when(carritoRepo.findByUsuarioId(1L)).thenReturn(Optional.of(carrito));
        when(pagoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var dto = new PagarRequestDTO(1L, "YAPE");
        var response = service.procesar(dto);

        assertEquals("YAPE", response.metodoPago());
        assertEquals("PENDIENTE", response.estado());
        assertEquals(0, BigDecimal.valueOf(20).compareTo(response.montoTotal()));
    }

    @Test
    void procesar_lanzaSiCarritoVacio() {
        when(carritoRepo.findByUsuarioId(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.procesar(new PagarRequestDTO(1L, "YAPE")));
    }

    @Test
    void procesar_lanzaSiMetodoInvalido() {
        Carrito carrito = crearCarritoConItems();
        when(carritoRepo.findByUsuarioId(1L)).thenReturn(Optional.of(carrito));
        assertThrows(IllegalArgumentException.class,
                () -> service.procesar(new PagarRequestDTO(1L, "BITCOIN")));
    }

    @Test
    void confirmar_creaVentaYCompletaPago() {
        Carrito carrito = crearCarritoConItems();
        Pago pago = new Pago();
        pago.setId(1L);
        pago.setCarrito(carrito);
        pago.setMetodoPago(MetodoPago.PLIN);
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setMontoTotal(BigDecimal.valueOf(20));

        when(pagoRepo.findById(1L)).thenReturn(Optional.of(pago));
        when(ventaRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pagoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(carritoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = service.confirmar(new ConfirmarPagoDTO(1L, "REF123"));
        assertEquals("testuser", response.usuario());
        assertEquals(1, response.libros().size());
        assertTrue(response.libros().get(0).contains("Libro Test"));

        ArgumentCaptor<Pago> pagoCaptor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepo).save(pagoCaptor.capture());
        assertEquals(EstadoPago.COMPLETADO, pagoCaptor.getValue().getEstado());
        assertEquals("REF123", pagoCaptor.getValue().getReferencia());
        assertTrue(carrito.getItems().isEmpty());
    }

    @Test
    void confirmar_lanzaSiPagoNoExiste() {
        when(pagoRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.confirmar(new ConfirmarPagoDTO(99L, null)));
    }

    @Test
    void confirmar_lanzaSiPagoYaCompletado() {
        Pago pago = new Pago();
        pago.setId(1L);
        pago.setEstado(EstadoPago.COMPLETADO);
        when(pagoRepo.findById(1L)).thenReturn(Optional.of(pago));
        assertThrows(IllegalStateException.class,
                () -> service.confirmar(new ConfirmarPagoDTO(1L, null)));
    }
}

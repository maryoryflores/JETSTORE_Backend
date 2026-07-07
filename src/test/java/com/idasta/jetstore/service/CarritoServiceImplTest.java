package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.AgregarAlCarritoDTO;
import com.idasta.jetstore.model.Carrito;
import com.idasta.jetstore.model.Libro;
import com.idasta.jetstore.model.Usuario;
import com.idasta.jetstore.repository.CarritoRepo;
import com.idasta.jetstore.repository.LibroRepo;
import com.idasta.jetstore.repository.UsuarioRepo;
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
class CarritoServiceImplTest {

    @Mock
    private CarritoRepo carritoRepo;
    @Mock
    private LibroRepo libroRepo;
    @Mock
    private UsuarioRepo usuarioRepo;

    @InjectMocks
    private CarritoServiceImpl service;

    @Test
    void agregar_creaCarritoSiNoExiste() {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("test");
        Libro libro = new Libro();
        libro.setId(1L);
        libro.setPrecio(BigDecimal.TEN);
        libro.setTitulo("Test");

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(libroRepo.findById(1L)).thenReturn(Optional.of(libro));
        when(carritoRepo.findByUsuarioId(1L)).thenReturn(Optional.empty());
        when(carritoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var dto = new AgregarAlCarritoDTO(1L, 1L, 2);
        var response = service.agregar(dto);

        assertNotNull(response);
        assertEquals(1, response.items().size());
        assertEquals(2, response.items().get(0).cantidad());
        verify(carritoRepo, times(2)).save(any());
    }

    @Test
    void agregar_acumulaSiLibroYaEnCarrito() {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("test");
        Libro libro = new Libro();
        libro.setId(1L);
        libro.setPrecio(BigDecimal.TEN);
        libro.setTitulo("Test");

        Carrito carritoExistente = new Carrito();
        carritoExistente.setUsuario(usuario);
        carritoExistente.agregarOActualizarItem(libro, 1);

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(libroRepo.findById(1L)).thenReturn(Optional.of(libro));
        when(carritoRepo.findByUsuarioId(1L)).thenReturn(Optional.of(carritoExistente));
        when(carritoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var dto = new AgregarAlCarritoDTO(1L, 1L, 3);
        var response = service.agregar(dto);

        assertEquals(1, response.items().size());
        assertEquals(4, response.items().get(0).cantidad());
    }

    @Test
    void ver_lanzaExcepcionSiCarritoVacio() {
        when(carritoRepo.findByUsuarioId(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.ver(99L));
    }

    @Test
    void eliminarItem_remueveYRetorna() {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("test");
        Libro libro1 = new Libro();
        libro1.setId(1L);
        libro1.setPrecio(BigDecimal.TEN);
        libro1.setTitulo("L1");
        Libro libro2 = new Libro();
        libro2.setId(2L);
        libro2.setPrecio(BigDecimal.valueOf(20));
        libro2.setTitulo("L2");

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.agregarOActualizarItem(libro1, 1);
        carrito.agregarOActualizarItem(libro2, 1);

        when(carritoRepo.findByUsuarioId(1L)).thenReturn(Optional.of(carrito));
        when(carritoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = service.eliminarItem(1L, 1L);

        assertEquals(1, response.items().size());
        assertEquals(2L, response.items().get(0).libroId());
    }
}

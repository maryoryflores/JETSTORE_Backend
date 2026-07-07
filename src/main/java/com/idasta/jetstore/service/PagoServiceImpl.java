package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.ConfirmarPagoDTO;
import com.idasta.jetstore.dto.PagarRequestDTO;
import com.idasta.jetstore.dto.PagoResponseDTO;
import com.idasta.jetstore.dto.VentaResponseDTO;
import com.idasta.jetstore.model.*;
import com.idasta.jetstore.repository.CarritoRepo;
import com.idasta.jetstore.repository.PagoRepo;
import com.idasta.jetstore.repository.VentaRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoServiceImpl implements PagoService {
    private final CarritoRepo carritoRepo;
    private final PagoRepo pagoRepo;
    private final VentaRepo ventaRepo;

    public PagoServiceImpl(CarritoRepo carritoRepo, PagoRepo pagoRepo, VentaRepo ventaRepo) {
        this.carritoRepo = carritoRepo;
        this.pagoRepo = pagoRepo;
        this.ventaRepo = ventaRepo;
    }

    @Transactional
    @Override
    public PagoResponseDTO procesar(PagarRequestDTO dto) {
        Carrito carrito = carritoRepo.findByUsuarioId(dto.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("El carrito esta vacio"));

        if(carrito.getItems().isEmpty()){
            throw new IllegalStateException("No hay libros en el carrito");
        }

        MetodoPago metodo;
        try {
            metodo = MetodoPago.valueOf(dto.metodoPago().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Metodo de pago invalido: use TARJETA, YAPE o PLIN");
        }

        Pago pago = new Pago();
        pago.setCarrito(carrito);
        pago.setMetodoPago(metodo);
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setMontoTotal(carrito.getTotal());
        pagoRepo.save(pago);

        return toPagoResponse(pago);
    }

    @Transactional
    @Override
    public VentaResponseDTO confirmar(ConfirmarPagoDTO dto) {
        Pago pago = pagoRepo.findById(dto.pagoId())
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));

        if(pago.getEstado() != EstadoPago.PENDIENTE){
            throw new IllegalStateException("El pago ya fue procesado o esta cancelado");
        }

        Carrito carrito = pago.getCarrito();
        Usuario usuario = carrito.getUsuario();

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setAplicaBono(false);
        venta.setFechaHora(LocalDateTime.now());

        for(CarritoItem item : carrito.getItems()){
            DetalleVenta detalle = new DetalleVenta();
            detalle.setLibro(item.getLibro());
            detalle.setCantidad(item.getCantidad());
            detalle.setSubTotal(item.getSubtotal());
            venta.agregarDetalle(detalle);
        }

        ventaRepo.save(venta);

        pago.setVenta(venta);
        pago.setEstado(EstadoPago.COMPLETADO);
        pago.setReferencia(dto.referencia());
        pago.setFechaConfirmacion(LocalDateTime.now());
        pagoRepo.save(pago);

        carrito.getItems().clear();
        carritoRepo.save(carrito);

        return toVentaResponse(venta, pago.getMetodoPago());
    }

    private PagoResponseDTO toPagoResponse(Pago pago) {
        return new PagoResponseDTO(
                pago.getId(),
                pago.getMetodoPago().name(),
                pago.getEstado().name(),
                pago.getMontoTotal(),
                pago.getReferencia(),
                pago.getFechaCreacion(),
                pago.getFechaConfirmacion()
        );
    }

    private VentaResponseDTO toVentaResponse(Venta venta, MetodoPago metodo) {
        List<String> libros = venta.getDetalles().stream()
                .map(d -> d.getLibro().getTitulo() + " x" + d.getCantidad())
                .toList();
        return new VentaResponseDTO(
                venta.getId(),
                venta.getUsuario().getNombreUsuario(),
                venta.getTotal(),
                venta.getFechaHora(),
                libros
        );
    }
}

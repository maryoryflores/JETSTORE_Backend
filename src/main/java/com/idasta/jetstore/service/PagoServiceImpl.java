package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.ConfirmarPagoDTO;
import com.idasta.jetstore.dto.MercadoPagoPreferenceDTO;
import com.idasta.jetstore.dto.PagarRequestDTO;
import com.idasta.jetstore.dto.PagoResponseDTO;
import com.idasta.jetstore.dto.VentaResponseDTO;
import com.idasta.jetstore.model.*;
import com.idasta.jetstore.repository.CarritoRepo;
import com.idasta.jetstore.repository.PagoRepo;
import com.idasta.jetstore.repository.VentaRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PagoServiceImpl implements PagoService {
    private final CarritoRepo carritoRepo;
    private final PagoRepo pagoRepo;
    private final VentaRepo ventaRepo;
    private final MercadoPagoService mercadoPagoService;

    @Value("${mercadopago.back-urls.success:http://localhost:3000/checkout/success}")
    private String backSuccess;
    @Value("${mercadopago.back-urls.failure:http://localhost:3000/checkout}")
    private String backFailure;
    @Value("${mercadopago.back-urls.pending:http://localhost:3000/checkout}")
    private String backPending;
    @Value("${mercadopago.notification-url:http://localhost:8080/pago/webhook}")
    private String notificationUrl;

    public PagoServiceImpl(CarritoRepo carritoRepo, PagoRepo pagoRepo, VentaRepo ventaRepo, MercadoPagoService mercadoPagoService) {
        this.carritoRepo = carritoRepo;
        this.pagoRepo = pagoRepo;
        this.ventaRepo = ventaRepo;
        this.mercadoPagoService = mercadoPagoService;
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
            throw new IllegalArgumentException("Metodo de pago invalido: use TARJETA, YAPE, PLIN o MERCADOPAGO");
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

    @Transactional
    @Override
    public MercadoPagoPreferenceDTO crearPreferenciaMercadoPago(PagarRequestDTO dto) {
        Carrito carrito = carritoRepo.findByUsuarioId(dto.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("El carrito esta vacio"));
        if (carrito.getItems().isEmpty()) {
            throw new IllegalStateException("No hay libros en el carrito");
        }

        Pago pago = new Pago();
        pago.setCarrito(carrito);
        pago.setMetodoPago(MetodoPago.MERCADOPAGO);
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setMontoTotal(carrito.getTotal());
        pagoRepo.save(pago);

        // external_reference = pagoId para identificarlo en el webhook
        String externalReference = String.valueOf(pago.getId());

        List<Map<String, Object>> items = new ArrayList<>();
        for (CarritoItem ci : carrito.getItems()) {
            Map<String, Object> item = Map.of(
                    "title", ci.getLibro().getTitulo(),
                    "quantity", ci.getCantidad(),
                    "unit_price", ci.getLibro().getPrecio(),
                    "currency_id", "PEN"
            );
            items.add(item);
        }

        Map<String, Object> preferenceData = Map.of(
                "items", items,
                "external_reference", externalReference,
                "back_urls", Map.of(
                        "success", backSuccess + "?pagoId=" + pago.getId(),
                        "failure", backFailure,
                        "pending", backPending
                ),
                "auto_return", "approved",
                "notification_url", notificationUrl,
                "statement_descriptor", "JETSTORE"
        );

        Map<String, Object> mpResponse = mercadoPagoService.crearPreferencia(preferenceData);

        String preferenceId = (String) mpResponse.get("id");
        String initPoint = (String) mpResponse.getOrDefault("init_point", mpResponse.get("sandbox_init_point"));
        String sandboxInitPoint = (String) mpResponse.get("sandbox_init_point");

        pago.setMpPreferenceId(preferenceId);
        pago.setMpInitPoint(initPoint);
        pagoRepo.save(pago);

        return new MercadoPagoPreferenceDTO(pago.getId(), preferenceId, initPoint, sandboxInitPoint, pago.getMontoTotal());
    }

    @Transactional
    @Override
    public VentaResponseDTO confirmarMercadoPago(String mpPaymentId, String externalReference) {
        if (externalReference == null || externalReference.isBlank()) {
            throw new IllegalArgumentException("external_reference requerido");
        }
        Long pagoId = Long.valueOf(externalReference);
        Pago pago = pagoRepo.findById(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado: " + pagoId));

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            // ya procesado, devolver venta existente si hay
            if (pago.getVenta() != null) {
                return toVentaResponse(pago.getVenta(), pago.getMetodoPago());
            }
            throw new IllegalStateException("El pago ya fue procesado");
        }

        // Validar con MP si hay paymentId (opcional, si no hay token se omite)
        if (mpPaymentId != null && !mpPaymentId.isBlank()) {
            Map<String, Object> paymentInfo = mercadoPagoService.obtenerPago(mpPaymentId);
            String status = paymentInfo != null ? (String) paymentInfo.get("status") : null;
            if (status != null && !status.equalsIgnoreCase("approved")) {
                throw new IllegalStateException("El pago de Mercado Pago no está aprobado. Estado: " + status);
            }
            pago.setMpPaymentId(mpPaymentId);
        }

        // Reutilizar lógica de confirmar: crear venta y completar pago
        Carrito carrito = pago.getCarrito();
        Usuario usuario = carrito.getUsuario();

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setAplicaBono(false);
        venta.setFechaHora(LocalDateTime.now());
        for (CarritoItem item : carrito.getItems()) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setLibro(item.getLibro());
            detalle.setCantidad(item.getCantidad());
            detalle.setSubTotal(item.getSubtotal());
            venta.agregarDetalle(detalle);
        }
        ventaRepo.save(venta);

        pago.setVenta(venta);
        pago.setEstado(EstadoPago.COMPLETADO);
        pago.setReferencia(mpPaymentId != null ? "MP-" + mpPaymentId : "MP-" + pago.getMpPreferenceId());
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

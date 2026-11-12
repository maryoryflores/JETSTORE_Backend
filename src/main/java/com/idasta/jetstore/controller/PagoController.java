package com.idasta.jetstore.controller;

import com.idasta.jetstore.dto.ConfirmarPagoDTO;
import com.idasta.jetstore.dto.PagarRequestDTO;
import com.idasta.jetstore.helper.RespuestaApi;
import com.idasta.jetstore.service.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pago")
public class PagoController {
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(@RequestBody PagarRequestDTO dto) {
        var pago = pagoService.procesar(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Pago registrado, confirme para completar", pago));
    }

    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarPago(@RequestBody ConfirmarPagoDTO dto) {
        var venta = pagoService.confirmar(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Pago confirmado, compra exitosa", venta));
    }

    @PostMapping("/crear-preferencia")
    public ResponseEntity<?> crearPreferencia(@RequestBody PagarRequestDTO dto) {
        var pref = pagoService.crearPreferenciaMercadoPago(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Preferencia Mercado Pago creada", pref));
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody Map<String, Object> payload, @RequestParam Map<String, String> params) {
        System.out.println("[MP Webhook] Payload: " + payload + " Params: " + params);
        // MP envía ?id=xxx&type=payment o JSON con data.id
        String paymentId = params.get("id");
        if (paymentId == null && payload.get("data") instanceof Map<?,?> data) {
            Object id = ((Map<?,?>) data).get("id");
            if (id != null) paymentId = id.toString();
        }
        if (paymentId == null) paymentId = (String) payload.get("id");
        String externalReference = null;
        if (payload.get("external_reference") != null) externalReference = payload.get("external_reference").toString();
        // Si viene solo paymentId, intentamos buscar pago por el id externo via MP API (opcional)
        try {
            if (externalReference != null) {
                pagoService.confirmarMercadoPago(paymentId, externalReference);
            }
        } catch (Exception e) {
            System.out.println("[MP Webhook] Error al confirmar: " + e.getMessage());
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/confirmar-mp")
    public ResponseEntity<?> confirmarMp(@RequestBody Map<String, String> body) {
        String pagoId = body.get("pagoId");
        String mpPaymentId = body.get("mpPaymentId");
        String externalReference = body.get("externalReference");
        if (externalReference == null) externalReference = pagoId;
        var venta = pagoService.confirmarMercadoPago(mpPaymentId, externalReference);
        return ResponseEntity.ok(new RespuestaApi(true, "Pago Mercado Pago confirmado", venta));
    }
}

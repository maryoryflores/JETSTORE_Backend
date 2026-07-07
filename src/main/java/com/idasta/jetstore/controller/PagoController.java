package com.idasta.jetstore.controller;

import com.idasta.jetstore.dto.ConfirmarPagoDTO;
import com.idasta.jetstore.dto.PagarRequestDTO;
import com.idasta.jetstore.helper.RespuestaApi;
import com.idasta.jetstore.service.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pago/")
public class PagoController {
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("procesar")
    public ResponseEntity<?> procesarPago(@RequestBody PagarRequestDTO dto) {
        var pago = pagoService.procesar(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Pago registrado, confirme para completar", pago));
    }

    @PostMapping("confirmar")
    public ResponseEntity<?> confirmarPago(@RequestBody ConfirmarPagoDTO dto) {
        var venta = pagoService.confirmar(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Pago confirmado, compra exitosa", venta));
    }
}

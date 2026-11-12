package com.idasta.jetstore.controller;

import com.idasta.jetstore.dto.RestablecerPasswordDTO;
import com.idasta.jetstore.dto.SolicitarRecuperacionDTO;
import com.idasta.jetstore.helper.RespuestaApi;
import com.idasta.jetstore.service.PasswordRecoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuario/")
public class PasswordRecoveryController {
    private final PasswordRecoveryService passwordRecoveryService;

    public PasswordRecoveryController(PasswordRecoveryService passwordRecoveryService) {
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @PostMapping("recuperar")
    public ResponseEntity<?> recuperar(@RequestBody SolicitarRecuperacionDTO dto) {
        passwordRecoveryService.solicitarRecuperacion(dto);
        return ResponseEntity.ok(new RespuestaApi(true,
                "Si el correo está registrado, recibirás instrucciones para recuperar tu contraseña", null));
    }

    @PostMapping("restablecer")
    public ResponseEntity<?> restablecer(@RequestBody RestablecerPasswordDTO dto) {
        passwordRecoveryService.restablecerPassword(dto);
        return ResponseEntity.ok(new RespuestaApi(true,
                "Tu contraseña ha sido actualizada correctamente", null));
    }
}
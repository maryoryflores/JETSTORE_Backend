package com.idasta.jetstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponseDTO(
        Long pagoId,
        String metodoPago,
        String estado,
        BigDecimal montoTotal,
        String referencia,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaConfirmacion
) {}

package com.idasta.jetstore.dto;

import java.math.BigDecimal;

public record MercadoPagoPreferenceDTO(
        Long pagoId,
        String preferenceId,
        String initPoint,
        String sandboxInitPoint,
        BigDecimal montoTotal
) {}

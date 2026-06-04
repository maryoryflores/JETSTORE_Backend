package com.idasta.jetstore.dto;

import java.math.BigDecimal;

public record VerLibroDTO(
        Long id,
        String titulo,
        String autor,
        String categoria,
        BigDecimal precio,
        String formato,
        int stock
) {
}

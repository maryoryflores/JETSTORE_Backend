package com.idasta.jetstore.dto;

import java.math.BigDecimal;

public record FiltroLibroDTO(
        String categoria,
        String formato,
        BigDecimal precioDesde,
        BigDecimal precioHasta
) {
}

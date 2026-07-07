package com.idasta.jetstore.dto;

import java.math.BigDecimal;

public record ItemCarritoDTO(
        Long itemId,
        Long libroId,
        String titulo,
        String autor,
        BigDecimal precioUnitario,
        int cantidad,
        BigDecimal subtotal
) {}

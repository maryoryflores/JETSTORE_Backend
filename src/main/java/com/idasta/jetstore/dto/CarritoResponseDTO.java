package com.idasta.jetstore.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarritoResponseDTO(
        Long carritoId,
        List<ItemCarritoDTO> items,
        BigDecimal total
) {}

package com.idasta.jetstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VerLibroDTO(
        Long id,
        String titulo,
        String autor,
        String categoria,
        BigDecimal precio,
        String formato,
        String imagen,
        int stock,
        LocalDateTime fechaCreacion
) {
}

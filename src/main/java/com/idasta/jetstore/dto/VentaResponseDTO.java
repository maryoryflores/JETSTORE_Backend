package com.idasta.jetstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponseDTO(
        Long ventaId,
        String usuario,
        BigDecimal total,
        LocalDateTime fechaHora,
        List<String> libros
) {}

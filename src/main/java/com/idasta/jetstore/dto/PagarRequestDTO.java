package com.idasta.jetstore.dto;

public record PagarRequestDTO(
        Long usuarioId,
        String metodoPago
) {}

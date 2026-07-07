package com.idasta.jetstore.dto;

public record LoginResponseDTO(
        String token,
        Long usuarioId,
        String nombreUsuario,
        String rol
) {}

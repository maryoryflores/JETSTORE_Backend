package com.idasta.jetstore.dto;

public record UsuarioPerfilDTO(
        Long id,
        String nombreUsuario,
        String correo,
        String rol
) {}

package com.idasta.jetstore.dto;

public record RegistrarUsuarioDTO(
        String nombreUsuario,
        String correo,
        String password,
        String rol
) {}

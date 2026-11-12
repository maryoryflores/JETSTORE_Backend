package com.idasta.jetstore.dto;

public record RestablecerPasswordDTO(
        String token,
        String password,
        String confirmPassword
) {}
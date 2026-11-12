package com.idasta.jetstore.dto;

public record CategoriaDTO(
        Long id,
        String nombre,
        String descripcion,
        long productCount
) {}

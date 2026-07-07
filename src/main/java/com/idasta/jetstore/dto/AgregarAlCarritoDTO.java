package com.idasta.jetstore.dto;

public record AgregarAlCarritoDTO(
        Long usuarioId,
        Long libroId,
        int cantidad
) {
    public AgregarAlCarritoDTO {
        if(cantidad < 1) cantidad = 1;
    }
}

package com.idasta.jetstore.dto;

public record AgregarStockDTO(
        Long libroId,
        int cantidad
) {
    public AgregarStockDTO {
        if(cantidad < 1) cantidad = 1;
    }
}

package com.idasta.jetstore.dto;

import com.idasta.jetstore.model.Categoria;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record GuardarLibroDTO(
        Long id,
        String titulo,
        String autor,
        GuardarCategoriaDTO categoria,
        BigDecimal precio,
        String formato,
        String imagen,
        int stock
) {
    public GuardarLibroDTO{
        if(precio==null || precio.compareTo(BigDecimal.ZERO)<0){
            precio = BigDecimal.ZERO;
        }else {
            precio = precio.setScale(2, RoundingMode.HALF_UP);
        }

        if(stock < 0){
            stock = 0;
        }
    }
}

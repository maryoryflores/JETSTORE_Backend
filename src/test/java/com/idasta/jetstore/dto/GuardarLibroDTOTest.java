package com.idasta.jetstore.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GuardarLibroDTOTest {

    @Test
    void constructor_normalizaPrecioNull() {
        var dto = new GuardarLibroDTO(null, "Titulo", "Autor", null, null, "PDF", null, 5);
        assertEquals(BigDecimal.ZERO, dto.precio());
    }

    @Test
    void constructor_normalizaPrecioNegativo() {
        var dto = new GuardarLibroDTO(null, "Titulo", "Autor", null, BigDecimal.valueOf(-10), "PDF", null, 5);
        assertEquals(BigDecimal.ZERO, dto.precio());
    }

    @Test
    void constructor_normalizaPrecioPositivo() {
        var dto = new GuardarLibroDTO(null, "Titulo", "Autor", null, BigDecimal.valueOf(25.5), "PDF", null, 5);
        assertEquals(0, dto.precio().compareTo(BigDecimal.valueOf(25.50)));
    }

    @Test
    void constructor_normalizaStockNegativo() {
        var dto = new GuardarLibroDTO(null, "Titulo", "Autor", null, BigDecimal.TEN, "PDF", null, -3);
        assertEquals(0, dto.stock());
    }

    @Test
    void constructor_mantieneStockValido() {
        var dto = new GuardarLibroDTO(null, "Titulo", "Autor", null, BigDecimal.TEN, "PDF", null, 7);
        assertEquals(7, dto.stock());
    }
}

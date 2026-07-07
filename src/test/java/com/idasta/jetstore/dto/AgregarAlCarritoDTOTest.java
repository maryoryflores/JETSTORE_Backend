package com.idasta.jetstore.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgregarAlCarritoDTOTest {

    @Test
    void constructor_normalizaCantidadCero() {
        var dto = new AgregarAlCarritoDTO(1L, 1L, 0);
        assertEquals(1, dto.cantidad());
    }

    @Test
    void constructor_normalizaCantidadNegativa() {
        var dto = new AgregarAlCarritoDTO(1L, 1L, -5);
        assertEquals(1, dto.cantidad());
    }

    @Test
    void constructor_mantieneCantidadValida() {
        var dto = new AgregarAlCarritoDTO(1L, 1L, 3);
        assertEquals(3, dto.cantidad());
    }
}

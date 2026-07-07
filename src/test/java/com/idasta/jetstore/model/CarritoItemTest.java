package com.idasta.jetstore.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CarritoItemTest {

    @Test
    void getSubtotal_calculaPrecioPorCantidad() {
        Libro libro = new Libro();
        libro.setPrecio(BigDecimal.valueOf(25));

        CarritoItem item = new CarritoItem();
        item.setLibro(libro);
        item.setCantidad(3);

        assertEquals(0, BigDecimal.valueOf(75).compareTo(item.getSubtotal()));
    }
}

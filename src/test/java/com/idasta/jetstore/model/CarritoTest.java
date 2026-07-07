package com.idasta.jetstore.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CarritoTest {

    private Libro crearLibro(Long id, BigDecimal precio) {
        Libro l = new Libro();
        l.setId(id);
        l.setPrecio(precio);
        l.setTitulo("Libro " + id);
        return l;
    }

    @Test
    void agregarOActualizarItem_agregaNuevo() {
        Carrito carrito = new Carrito();
        Libro libro = crearLibro(1L, BigDecimal.TEN);

        carrito.agregarOActualizarItem(libro, 2);

        assertEquals(1, carrito.getItems().size());
        assertEquals(2, carrito.getItems().get(0).getCantidad());
    }

    @Test
    void agregarOActualizarItem_acumulaSiExiste() {
        Carrito carrito = new Carrito();
        Libro libro = crearLibro(1L, BigDecimal.TEN);

        carrito.agregarOActualizarItem(libro, 2);
        carrito.agregarOActualizarItem(libro, 3);

        assertEquals(1, carrito.getItems().size());
        assertEquals(5, carrito.getItems().get(0).getCantidad());
    }

    @Test
    void eliminarItem_remuevePorLibroId() {
        Carrito carrito = new Carrito();
        carrito.agregarOActualizarItem(crearLibro(1L, BigDecimal.TEN), 1);
        carrito.agregarOActualizarItem(crearLibro(2L, BigDecimal.valueOf(20)), 1);

        carrito.eliminarItem(1L);

        assertEquals(1, carrito.getItems().size());
        assertEquals(2L, carrito.getItems().get(0).getLibro().getId());
    }

    @Test
    void getTotal_sumaSubtotales() {
        Carrito carrito = new Carrito();
        carrito.agregarOActualizarItem(crearLibro(1L, BigDecimal.TEN), 2);
        carrito.agregarOActualizarItem(crearLibro(2L, BigDecimal.valueOf(15)), 3);

        assertEquals(0, BigDecimal.valueOf(65).compareTo(carrito.getTotal()));
    }

    @Test
    void getTotal_carritoVacio() {
        Carrito carrito = new Carrito();
        assertEquals(BigDecimal.ZERO, carrito.getTotal());
    }
}

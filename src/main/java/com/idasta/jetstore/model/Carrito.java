package com.idasta.jetstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "carrito")
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoItem> items = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    public Carrito() {}

    public void agregarOActualizarItem(Libro libro, int cantidad) {
        for(CarritoItem item : items) {
            if(item.getLibro().getId().equals(libro.getId())) {
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        CarritoItem nuevo = new CarritoItem();
        nuevo.setCarrito(this);
        nuevo.setLibro(libro);
        nuevo.setCantidad(cantidad);
        items.add(nuevo);
    }

    public void eliminarItem(Long libroId) {
        items.removeIf(item -> item.getLibro().getId().equals(libroId));
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PrePersist
    private void prePersist() {
        fechaCreacion = LocalDateTime.now();
    }
}

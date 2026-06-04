package com.idasta.jetstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {
    public DetalleVenta() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @Column(nullable = false)
    @Min(1)
    private int cantidad;

    @Column(nullable = false)
    @NotNull
    private BigDecimal subTotal;

    //PREPERSIST PREUPDATE-----------------------------------------------------------------
    @PrePersist
    private void prePersist() {
        calcularTotal();
    }

    @PreUpdate
    private void preUpdate() {
        calcularTotal();
    }

    //METODOS ------------------------------------------------------------------------------
    private void calcularTotal() {
        this.subTotal = libro.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }
}

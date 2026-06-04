package com.idasta.jetstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @OneToOne(optional = false)
    @NotNull
    private Usuario usuario;

    @Setter
    @OneToMany(mappedBy = "venta",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal total;

    @Setter
    @Column(nullable = false)
    private boolean aplicaBono;

    @Setter
    @Column(nullable = false)
    @NotNull
    private LocalDateTime fechaHora;

    //PREPERSIS Y PREUPDATE---------------------------------------------------------
    @PrePersist
    private void prePersist(){
        calcularTotal();
    }

    @PreUpdate
    private void preUpdate(){
        calcularTotal();
    }

    //METODOS---------------------------------------------------------------------
    private void calcularTotal(){
        BigDecimal total = BigDecimal.ZERO;
        for(DetalleVenta detalle : this.detalles){
            total = total.add(detalle.getSubTotal());
        }

        this.total = total;
    }

    //HELPERS--------------------------------------------------------------------
    public void agregarDetalle(DetalleVenta detalle){
        detalle.setVenta(this);
        detalles.add(detalle);
        calcularTotal();
    }
}

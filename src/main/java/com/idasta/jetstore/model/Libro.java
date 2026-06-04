package com.idasta.jetstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Size(max = 255)
    private String titulo;

    @Column
    @Size(max = 255)
    private String autor;

    @ManyToOne(optional = false)
    private Categoria categoria;

    @Column(precision = 10, scale = 2)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal precio;

    @Column
    @Size(max = 255)
    private String formato;

    @Column
    @Min(0)
    private int stock;

    public Libro(){}
}

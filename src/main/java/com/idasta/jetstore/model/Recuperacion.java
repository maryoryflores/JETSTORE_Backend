package com.idasta.jetstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "recuperacion")
public class Recuperacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(nullable = false)
    private boolean utilizado;

    public Recuperacion() {}

    @PrePersist
    private void prePersist() {
        if(token == null) token = UUID.randomUUID().toString();
        if(fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if(fechaExpiracion == null) fechaExpiracion = fechaCreacion.plusHours(1);
        utilizado = false;
    }
}
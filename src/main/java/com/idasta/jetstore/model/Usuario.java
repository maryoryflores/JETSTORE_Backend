package com.idasta.jetstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Usuario {
    public Usuario(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column
    @Size(max = 255)
    private String nombreUsuario;

    @Setter
    @Column
    @Email
    private String correo;

    @Setter
    @Column
    private String passwordHash;

    @Setter
    @ManyToOne(optional = false)
    private Rol rol;
}

package com.idasta.jetstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 255)
    private String nombres;

    @Column(nullable = false)
    @Size(max = 255)
    private String apellidos;

    @Column
    @Size(max = 8)
    private String dni;

    @Column
    @Email
    private String correo;

    @Column
    @Size(max = 15)
    @Pattern(regexp = "^[0-9+]+$")
    private String telefono;

    public Cliente(){}
}

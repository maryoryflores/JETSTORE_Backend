package com.idasta.jetstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ClienteVenta {
    public ClienteVenta(){}
    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String nombres;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String apellidos;

    @Column
    @Email
    private String correo;

    @Column
    @Size(max = 15)
    private String telefono;
}

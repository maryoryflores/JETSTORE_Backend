package com.idasta.jetstore.helper;

import com.idasta.jetstore.dto.GuardarLibroDTO;

public class Validar {
    public static void GuardarLibroDTO(GuardarLibroDTO dto){
        if(Util.esNullVacio(dto.autor())){
            throw new IllegalArgumentException("Validar Guardar Libro DTO: El autor es null o vacio, porfavor ingrese un nombre de autor valido");
        } else if (Util.esNullVacio(dto.titulo())) {
            throw new IllegalArgumentException("Validar Guardar Libro DTO: El titulo no puede estar vacio");
        } else if(Util.esNullVacio(dto.formato())){
            throw new IllegalArgumentException("Validar Guardar Libro DTO: Porfavor ingrese un formato");
        }
    }
}

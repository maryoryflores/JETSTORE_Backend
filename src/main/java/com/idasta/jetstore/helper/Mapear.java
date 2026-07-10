package com.idasta.jetstore.helper;

import com.idasta.jetstore.dto.GuardarCategoriaDTO;
import com.idasta.jetstore.dto.GuardarLibroDTO;
import com.idasta.jetstore.model.Categoria;
import com.idasta.jetstore.model.Libro;

public class Mapear {
    public static Libro libro(GuardarLibroDTO dto){
        Libro libro = new Libro();
        libro.setTitulo(dto.titulo());
        libro.setAutor(dto.autor());
        libro.setPrecio(dto.precio());
        libro.setFormato(dto.formato());
        libro.setImagen(dto.imagen());
        libro.setStock(dto.stock());

        return libro;
    }

    public static Categoria categoria(GuardarCategoriaDTO dto){
        Categoria cat = new Categoria();
        cat.setNombre(dto.nombre());
        return cat;
    }
}

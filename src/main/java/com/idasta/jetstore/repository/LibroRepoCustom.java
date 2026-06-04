package com.idasta.jetstore.repository;

import com.idasta.jetstore.dto.VerLibroDTO;
import com.idasta.jetstore.model.Categoria;

import java.util.List;

public interface LibroRepoCustom {
    void crearCategoria(Categoria cat);

    boolean existeCategoria(String nombre);

    Categoria CategoriaGenerica();

    List<Categoria> buscarCategoriaNombre(String nombre);

    List<VerLibroDTO> listarLibros();
}
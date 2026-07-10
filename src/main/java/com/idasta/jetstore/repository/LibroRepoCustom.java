package com.idasta.jetstore.repository;

import com.idasta.jetstore.dto.FiltroLibroDTO;
import com.idasta.jetstore.dto.VerLibroDTO;
import com.idasta.jetstore.model.Categoria;
import com.idasta.jetstore.model.Libro;

import java.util.List;

public interface LibroRepoCustom {
    void crearCategoria(Categoria cat);

    boolean existeCategoria(String nombre);

    Categoria CategoriaGenerica();

    List<Categoria> buscarCategoriaNombre(String nombre);

    List<Categoria> listarCategorias();

    VerLibroDTO obtenerLibroPorId(Long id);

    List<VerLibroDTO> listarLibros();

    List<Libro> filtrarLibros(FiltroLibroDTO dto);

    List<Libro> buscarPorKeyword(String keyword);

    List<Libro> listarPorCategoria(String categoria);

    List<Libro> listarMasRecientes();
}
package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.CategoriaDTO;
import com.idasta.jetstore.dto.GuardarCategoriaDTO;

import java.util.List;

public interface CategoriaService {
    List<CategoriaDTO> listarTodas();
    CategoriaDTO guardar(GuardarCategoriaDTO dto);
    CategoriaDTO actualizar(Long id, GuardarCategoriaDTO dto);
    void eliminar(Long id);
}

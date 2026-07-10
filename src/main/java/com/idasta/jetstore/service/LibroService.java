package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.AgregarStockDTO;
import com.idasta.jetstore.dto.FiltroLibroDTO;
import com.idasta.jetstore.dto.GuardarLibroDTO;
import com.idasta.jetstore.dto.VerLibroDTO;

import java.util.List;

public interface LibroService {
    void guardar(GuardarLibroDTO dto);
    void agregarStock(AgregarStockDTO dto);
    void eliminar(Long libroId);
    List<VerLibroDTO> filtrarLibros(FiltroLibroDTO dto);
    List<VerLibroDTO> verLibros();
    List<VerLibroDTO> buscar(String keyword);
    List<VerLibroDTO> listarPorCategoria(String categoria);
    List<VerLibroDTO> listarMasRecientes();
    VerLibroDTO obtenerPorId(Long id);
    List<String> listarCategorias();
}

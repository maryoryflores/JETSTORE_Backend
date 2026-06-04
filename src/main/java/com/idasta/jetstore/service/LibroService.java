package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.GuardarLibroDTO;
import com.idasta.jetstore.dto.VerLibroDTO;

import java.util.List;

public interface LibroService {
    void guardar(GuardarLibroDTO dto);
    List<VerLibroDTO> verLibros();
}

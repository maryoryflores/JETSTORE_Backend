package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.AgregarAlCarritoDTO;
import com.idasta.jetstore.dto.CarritoResponseDTO;

public interface CarritoService {
    CarritoResponseDTO agregar(AgregarAlCarritoDTO dto);
    CarritoResponseDTO ver(Long usuarioId);
    CarritoResponseDTO eliminarItem(Long usuarioId, Long libroId);
}

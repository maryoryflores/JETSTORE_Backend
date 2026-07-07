package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.ConfirmarPagoDTO;
import com.idasta.jetstore.dto.PagarRequestDTO;
import com.idasta.jetstore.dto.PagoResponseDTO;
import com.idasta.jetstore.dto.VentaResponseDTO;

public interface PagoService {
    PagoResponseDTO procesar(PagarRequestDTO dto);
    VentaResponseDTO confirmar(ConfirmarPagoDTO dto);
}

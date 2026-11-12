package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.RestablecerPasswordDTO;
import com.idasta.jetstore.dto.SolicitarRecuperacionDTO;

public interface PasswordRecoveryService {
    void solicitarRecuperacion(SolicitarRecuperacionDTO dto);
    void restablecerPassword(RestablecerPasswordDTO dto);
}
package com.idasta.jetstore.service;

public interface CorreoService {
    void enviarCorreoRecuperacion(String destinatario, String nombreUsuario, String enlace);
}
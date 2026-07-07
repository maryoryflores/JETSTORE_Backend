package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.*;

public interface UsuarioService {
    UsuarioPerfilDTO registrar(RegistrarUsuarioDTO dto);
    LoginResponseDTO login(LoginDTO dto);
    UsuarioPerfilDTO obtenerPerfil(Long usuarioId);
    void cerrarSesion(String token);
}

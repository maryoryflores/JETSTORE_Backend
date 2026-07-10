package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.*;

import java.util.List;

public interface UsuarioService {
    UsuarioPerfilDTO registrar(RegistrarUsuarioDTO dto);
    LoginResponseDTO login(LoginDTO dto);
    UsuarioPerfilDTO obtenerPerfil(Long usuarioId);
    void cerrarSesion(String token);
    List<UsuarioPerfilDTO> listarUsuarios();
}

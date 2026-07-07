package com.idasta.jetstore.controller;

import com.idasta.jetstore.dto.LoginDTO;
import com.idasta.jetstore.dto.RegistrarUsuarioDTO;
import com.idasta.jetstore.helper.RespuestaApi;
import com.idasta.jetstore.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuario/")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("registrar")
    public ResponseEntity<?> registrar(@RequestBody RegistrarUsuarioDTO dto) {
        var perfil = usuarioService.registrar(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Usuario registrado correctamente", perfil));
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        var response = usuarioService.login(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Inicio de sesion exitoso", response));
    }

    @GetMapping("perfil/{usuarioId}")
    public ResponseEntity<?> perfil(@PathVariable Long usuarioId) {
        var perfil = usuarioService.obtenerPerfil(usuarioId);
        return ResponseEntity.ok(new RespuestaApi(true, "Perfil obtenido", perfil));
    }

    @PostMapping("cerrar-sesion")
    public ResponseEntity<?> cerrarSesion(@RequestHeader("Authorization") String token) {
        usuarioService.cerrarSesion(token);
        return ResponseEntity.ok(new RespuestaApi(true, "Sesion cerrada", null));
    }
}

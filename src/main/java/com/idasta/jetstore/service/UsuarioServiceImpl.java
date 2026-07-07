package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.*;
import com.idasta.jetstore.helper.PasswordUtil;
import com.idasta.jetstore.model.Rol;
import com.idasta.jetstore.model.Sesion;
import com.idasta.jetstore.model.Usuario;
import com.idasta.jetstore.repository.RolRepo;
import com.idasta.jetstore.repository.SesionRepo;
import com.idasta.jetstore.repository.UsuarioRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepo usuarioRepo;
    private final RolRepo rolRepo;
    private final SesionRepo sesionRepo;

    public UsuarioServiceImpl(UsuarioRepo usuarioRepo, RolRepo rolRepo, SesionRepo sesionRepo) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
        this.sesionRepo = sesionRepo;
    }

    @Transactional
    @Override
    public UsuarioPerfilDTO registrar(RegistrarUsuarioDTO dto) {
        if(usuarioRepo.findByNombreUsuario(dto.nombreUsuario()).isPresent()){
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        Rol rol = rolRepo.findByNombre(dto.rol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + dto.rol()));

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.nombreUsuario());
        usuario.setCorreo(dto.correo());
        usuario.setPasswordHash(PasswordUtil.hash(dto.password()));
        usuario.setRol(rol);
        usuarioRepo.save(usuario);

        return toPerfil(usuario);
    }

    @Transactional
    @Override
    public LoginResponseDTO login(LoginDTO dto) {
        Usuario usuario = usuarioRepo.findByNombreUsuario(dto.nombreUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña incorrectos"));

        if(!PasswordUtil.verify(dto.password(), usuario.getPasswordHash())){
            throw new IllegalArgumentException("Usuario o contraseña incorrectos");
        }

        Sesion sesion = new Sesion();
        sesion.setUsuario(usuario);
        sesionRepo.save(sesion);

        return new LoginResponseDTO(
                sesion.getToken(),
                usuario.getId(),
                usuario.getNombreUsuario(),
                usuario.getRol().getNombre()
        );
    }

    @Override
    public UsuarioPerfilDTO obtenerPerfil(Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return toPerfil(usuario);
    }

    @Transactional
    @Override
    public void cerrarSesion(String token) {
        sesionRepo.findByTokenAndActivaTrue(token).ifPresent(sesion -> {
            sesion.setActiva(false);
            sesionRepo.save(sesion);
        });
    }

    private UsuarioPerfilDTO toPerfil(Usuario usuario) {
        return new UsuarioPerfilDTO(
                usuario.getId(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getRol().getNombre()
        );
    }
}

package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.RestablecerPasswordDTO;
import com.idasta.jetstore.dto.SolicitarRecuperacionDTO;
import com.idasta.jetstore.helper.PasswordUtil;
import com.idasta.jetstore.model.Recuperacion;
import com.idasta.jetstore.model.Sesion;
import com.idasta.jetstore.model.Usuario;
import com.idasta.jetstore.repository.RecuperacionRepo;
import com.idasta.jetstore.repository.SesionRepo;
import com.idasta.jetstore.repository.UsuarioRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private static final int LONGITUD_MINIMA_PASSWORD = 6;
    private static final Pattern CORREO_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioRepo usuarioRepo;
    private final RecuperacionRepo recuperacionRepo;
    private final SesionRepo sesionRepo;
    private final CorreoService correoService;
    private final String frontendUrl;
    private final long expiracionHoras;

    public PasswordRecoveryServiceImpl(
            UsuarioRepo usuarioRepo,
            RecuperacionRepo recuperacionRepo,
            SesionRepo sesionRepo,
            CorreoService correoService,
            @Value("${jetstore.frontend-url:http://localhost:3000}") String frontendUrl,
            @Value("${jetstore.recuperacion.expiracion-horas:1}") long expiracionHoras) {
        this.usuarioRepo = usuarioRepo;
        this.recuperacionRepo = recuperacionRepo;
        this.sesionRepo = sesionRepo;
        this.correoService = correoService;
        this.frontendUrl = frontendUrl;
        this.expiracionHoras = expiracionHoras;
    }

    @Override
    @Transactional
    public void solicitarRecuperacion(SolicitarRecuperacionDTO dto) {
        String correo = dto.correo() == null ? "" : dto.correo().trim();
        if(correo.isBlank() || !CORREO_PATTERN.matcher(correo).matches()){
            throw new IllegalArgumentException("Ingresa un correo electrónico válido.");
        }

        List<Usuario> usuarios = usuarioRepo.findAllByCorreo(correo);
        for (Usuario usuario : usuarios) {
            invalidarTokensPrevios(usuario.getId());

            Recuperacion recuperacion = new Recuperacion();
            recuperacion.setUsuario(usuario);
            recuperacion.setFechaExpiracion(LocalDateTime.now().plusHours(expiracionHoras));
            recuperacionRepo.save(recuperacion);

            String enlace = frontendUrl + "/reset-password?token=" + recuperacion.getToken();
            correoService.enviarCorreoRecuperacion(usuario.getCorreo(), usuario.getNombreUsuario(), enlace);
        }
    }

    @Override
    @Transactional
    public void restablecerPassword(RestablecerPasswordDTO dto) {
        String token = dto.token() == null ? "" : dto.token().trim();
        String password = dto.password() == null ? "" : dto.password();
        String confirmPassword = dto.confirmPassword() == null ? "" : dto.confirmPassword();

        if(token.isBlank()){
            throw new IllegalArgumentException("El enlace de recuperación no es válido.");
        }
        if(password.length() < LONGITUD_MINIMA_PASSWORD){
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        if(!password.equals(confirmPassword)){
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        Recuperacion recuperacion = recuperacionRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("El enlace de recuperación no es válido."));

        if(recuperacion.isUtilizado()){
            throw new IllegalArgumentException("El enlace de recuperación no es válido.");
        }
        if(recuperacion.getFechaExpiracion().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("El enlace de recuperación ha expirado. Solicita uno nuevo.");
        }

        Usuario usuario = recuperacion.getUsuario();
        usuario.setPasswordHash(PasswordUtil.hash(password));
        usuarioRepo.save(usuario);

        invalidarSesionesActivas(usuario.getId());

        recuperacion.setUtilizado(true);
        recuperacionRepo.save(recuperacion);
    }

    private void invalidarTokensPrevios(Long usuarioId) {
        for (Recuperacion previa : recuperacionRepo.findByUsuarioId(usuarioId)) {
            if(!previa.isUtilizado()){
                previa.setUtilizado(true);
                recuperacionRepo.save(previa);
            }
        }
    }

    private void invalidarSesionesActivas(Long usuarioId) {
        for (Sesion sesion : sesionRepo.findByUsuarioIdAndActivaTrue(usuarioId)) {
            sesion.setActiva(false);
            sesionRepo.save(sesion);
        }
    }
}
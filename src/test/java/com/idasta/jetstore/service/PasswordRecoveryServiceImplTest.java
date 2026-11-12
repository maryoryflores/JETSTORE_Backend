package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.RestablecerPasswordDTO;
import com.idasta.jetstore.dto.SolicitarRecuperacionDTO;
import com.idasta.jetstore.model.Recuperacion;
import com.idasta.jetstore.model.Sesion;
import com.idasta.jetstore.model.Usuario;
import com.idasta.jetstore.repository.RecuperacionRepo;
import com.idasta.jetstore.repository.SesionRepo;
import com.idasta.jetstore.repository.UsuarioRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceImplTest {

    @Mock
    private UsuarioRepo usuarioRepo;
    @Mock
    private RecuperacionRepo recuperacionRepo;
    @Mock
    private SesionRepo sesionRepo;
    @Mock
    private CorreoService correoService;

    private PasswordRecoveryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PasswordRecoveryServiceImpl(
                usuarioRepo, recuperacionRepo, sesionRepo, correoService,
                "http://localhost:3000", 1L);
    }

    private Usuario usuario(String nombre, String correo) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombre);
        usuario.setCorreo(correo);
        return usuario;
    }

    private Recuperacion recuperacionValida(Usuario usuario, boolean utilizado, int horasExpiracion) {
        Recuperacion r = new Recuperacion();
        r.setUsuario(usuario);
        r.setUtilizado(utilizado);
        r.setFechaExpiracion(LocalDateTime.now().plusHours(horasExpiracion));
        return r;
    }

    @Test
    void solicitar_conCorreoRegistrado_generaTokenYEnviaCorreo() {
        Usuario usuario = usuario("juan", "juan@correo.com");
        when(usuarioRepo.findAllByCorreo("juan@correo.com")).thenReturn(List.of(usuario));
        when(recuperacionRepo.findByUsuarioId(any())).thenReturn(List.of());
        when(recuperacionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.solicitarRecuperacion(new SolicitarRecuperacionDTO(" juan@correo.com "));

        verify(recuperacionRepo).save(any(Recuperacion.class));
        verify(correoService).enviarCorreoRecuperacion(
                eq("juan@correo.com"), eq("juan"), contains("/reset-password?token="));
    }

    @Test
    void solicitar_conCorreoDuplicado_generaUnTokenPorCuenta() {
        Usuario u1 = usuario("u1", "dup@correo.com");
        Usuario u2 = usuario("u2", "dup@correo.com");
        when(usuarioRepo.findAllByCorreo("dup@correo.com")).thenReturn(List.of(u1, u2));
        when(recuperacionRepo.findByUsuarioId(any())).thenReturn(List.of());
        when(recuperacionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.solicitarRecuperacion(new SolicitarRecuperacionDTO("dup@correo.com"));

        verify(recuperacionRepo, times(2)).save(any(Recuperacion.class));
        verify(correoService, times(2)).enviarCorreoRecuperacion(anyString(), anyString(), anyString());
    }

    @Test
    void solicitar_conCorreoNoRegistrado_noLanzaNiEnvia() {
        when(usuarioRepo.findAllByCorreo("nadie@correo.com")).thenReturn(List.of());

        assertDoesNotThrow(() -> service.solicitarRecuperacion(new SolicitarRecuperacionDTO("nadie@correo.com")));
        verifyNoInteractions(correoService);
    }

    @Test
    void solicitar_conCorreoInvalido_lanza() {
        assertThrows(IllegalArgumentException.class,
                () -> service.solicitarRecuperacion(new SolicitarRecuperacionDTO("correo-invalido")));
        verifyNoInteractions(usuarioRepo);
    }

    @Test
    void solicitar_conCorreoVacio_lanza() {
        assertThrows(IllegalArgumentException.class,
                () -> service.solicitarRecuperacion(new SolicitarRecuperacionDTO("   ")));
    }

    @Test
    void solicitar_invalidaTokensPreviosDelUsuario() {
        Usuario usuario = usuario("juan", "juan@correo.com");
        Recuperacion previa = new Recuperacion();
        previa.setUtilizado(false);
        when(usuarioRepo.findAllByCorreo("juan@correo.com")).thenReturn(List.of(usuario));
        when(recuperacionRepo.findByUsuarioId(usuario.getId())).thenReturn(List.of(previa));
        when(recuperacionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.solicitarRecuperacion(new SolicitarRecuperacionDTO("juan@correo.com"));

        assertTrue(previa.isUtilizado());
    }

    @Test
    void restablecer_tokenValido_actualizaPasswordYMarcaToken() {
        Usuario usuario = usuario("juan", "juan@correo.com");
        usuario.setPasswordHash("hash-viejo");
        Recuperacion r = recuperacionValida(usuario, false, 1);
        when(recuperacionRepo.findByToken("tok")).thenReturn(Optional.of(r));
        when(usuarioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(sesionRepo.findByUsuarioIdAndActivaTrue(any())).thenReturn(List.of());

        service.restablecerPassword(new RestablecerPasswordDTO("tok", "nueva123", "nueva123"));

        assertNotEquals("hash-viejo", usuario.getPasswordHash());
        assertNotEquals("nueva123", usuario.getPasswordHash());
        assertTrue(r.isUtilizado());
        verify(usuarioRepo).save(usuario);
        verify(recuperacionRepo).save(r);
    }

    @Test
    void restablecer_tokenValido_invalidaSesionesActivas() {
        Usuario usuario = usuario("juan", "juan@correo.com");
        Recuperacion r = recuperacionValida(usuario, false, 1);
        Sesion s1 = new Sesion();
        s1.setActiva(true);
        when(recuperacionRepo.findByToken("tok")).thenReturn(Optional.of(r));
        when(usuarioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(sesionRepo.findByUsuarioIdAndActivaTrue(usuario.getId())).thenReturn(List.of(s1));

        service.restablecerPassword(new RestablecerPasswordDTO("tok", "nueva123", "nueva123"));

        assertFalse(s1.isActiva());
    }

    @Test
    void restablecer_tokenInvalido_lanza() {
        when(recuperacionRepo.findByToken("no-existe")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.restablecerPassword(new RestablecerPasswordDTO("no-existe", "nueva123", "nueva123")));
    }

    @Test
    void restablecer_tokenUsado_lanza() {
        Usuario usuario = usuario("juan", "juan@correo.com");
        when(recuperacionRepo.findByToken("tok")).thenReturn(Optional.of(recuperacionValida(usuario, true, 1)));

        assertThrows(IllegalArgumentException.class,
                () -> service.restablecerPassword(new RestablecerPasswordDTO("tok", "nueva123", "nueva123")));
    }

    @Test
    void restablecer_tokenExpirado_lanza() {
        Usuario usuario = usuario("juan", "juan@correo.com");
        when(recuperacionRepo.findByToken("tok")).thenReturn(Optional.of(recuperacionValida(usuario, false, -1)));

        assertThrows(IllegalArgumentException.class,
                () -> service.restablecerPassword(new RestablecerPasswordDTO("tok", "nueva123", "nueva123")));
    }

    @Test
    void restablecer_passwordCorta_lanza() {
        assertThrows(IllegalArgumentException.class,
                () -> service.restablecerPassword(new RestablecerPasswordDTO("tok", "123", "123")));
    }

    @Test
    void restablecer_passwordDiferente_lanza() {
        assertThrows(IllegalArgumentException.class,
                () -> service.restablecerPassword(new RestablecerPasswordDTO("tok", "nueva123", "distinta1")));
    }

    @Test
    void restablecer_tokenVacio_lanza() {
        assertThrows(IllegalArgumentException.class,
                () -> service.restablecerPassword(new RestablecerPasswordDTO("   ", "nueva123", "nueva123")));
    }
}
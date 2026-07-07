package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.LoginDTO;
import com.idasta.jetstore.dto.RegistrarUsuarioDTO;
import com.idasta.jetstore.model.Rol;
import com.idasta.jetstore.model.Usuario;
import com.idasta.jetstore.repository.RolRepo;
import com.idasta.jetstore.repository.SesionRepo;
import com.idasta.jetstore.repository.UsuarioRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepo usuarioRepo;
    @Mock
    private RolRepo rolRepo;
    @Mock
    private SesionRepo sesionRepo;

    @InjectMocks
    private UsuarioServiceImpl service;

    @Test
    void registrar_creaUsuario() {
        when(usuarioRepo.findByNombreUsuario("testuser")).thenReturn(Optional.empty());
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("USER");
        when(rolRepo.findByNombre("USER")).thenReturn(Optional.of(rol));
        when(usuarioRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var dto = new RegistrarUsuarioDTO("testuser", "test@mail.com", "password", "USER");
        var perfil = service.registrar(dto);

        assertEquals("testuser", perfil.nombreUsuario());
        assertEquals("USER", perfil.rol());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepo).save(captor.capture());
        assertNotNull(captor.getValue().getPasswordHash());
        assertNotEquals("password", captor.getValue().getPasswordHash());
    }

    @Test
    void registrar_lanzaSiUsuarioExiste() {
        when(usuarioRepo.findByNombreUsuario("exists")).thenReturn(Optional.of(new Usuario()));
        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(new RegistrarUsuarioDTO("exists", "a@b.com", "pass", "USER")));
    }

    @Test
    void registrar_lanzaSiRolNoExiste() {
        when(usuarioRepo.findByNombreUsuario("nuevo")).thenReturn(Optional.empty());
        when(rolRepo.findByNombre("INEXISTENTE")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(new RegistrarUsuarioDTO("nuevo", "a@b.com", "pass", "INEXISTENTE")));
    }

    @Test
    void login_lanzaSiPasswordIncorrecto() {
        Rol rol = new Rol();
        rol.setNombre("USER");

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("testuser");
        usuario.setPasswordHash("hash");
        usuario.setRol(rol);

        when(usuarioRepo.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuario));

        var dto = new LoginDTO("testuser", "wrong");
        assertThrows(IllegalArgumentException.class, () -> service.login(dto));
    }
}

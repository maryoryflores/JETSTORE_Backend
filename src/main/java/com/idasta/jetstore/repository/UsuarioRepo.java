package com.idasta.jetstore.repository;

import com.idasta.jetstore.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByCorreo(String correo);
    List<Usuario> findAllByOrderByNombreUsuarioAsc();
}

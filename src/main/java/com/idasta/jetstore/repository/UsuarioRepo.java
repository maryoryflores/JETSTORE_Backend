package com.idasta.jetstore.repository;

import com.idasta.jetstore.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {
    java.util.Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}

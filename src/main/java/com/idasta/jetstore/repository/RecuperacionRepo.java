package com.idasta.jetstore.repository;

import com.idasta.jetstore.model.Recuperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecuperacionRepo extends JpaRepository<Recuperacion, Long> {
    Optional<Recuperacion> findByToken(String token);
    List<Recuperacion> findByUsuarioId(Long usuarioId);
}
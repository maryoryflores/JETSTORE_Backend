package com.idasta.jetstore.repository;

import com.idasta.jetstore.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SesionRepo extends JpaRepository<Sesion, Long> {
    Optional<Sesion> findByTokenAndActivaTrue(String token);
}

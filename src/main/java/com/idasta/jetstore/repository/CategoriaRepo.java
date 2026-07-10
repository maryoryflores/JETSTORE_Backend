package com.idasta.jetstore.repository;

import com.idasta.jetstore.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepo extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
}

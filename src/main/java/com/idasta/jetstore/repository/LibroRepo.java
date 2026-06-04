package com.idasta.jetstore.repository;

import com.idasta.jetstore.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepo extends LibroRepoCustom,JpaRepository<Libro, Long> {
}

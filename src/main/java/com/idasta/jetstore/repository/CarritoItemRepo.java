package com.idasta.jetstore.repository;

import com.idasta.jetstore.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoItemRepo extends JpaRepository<CarritoItem, Long> {
}

package com.idasta.jetstore.repository;

import com.idasta.jetstore.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagoRepo extends JpaRepository<Pago, Long> {
    Optional<Pago> findByCarritoUsuarioIdAndEstado(Long usuarioId, String estado);
}

package com.es2.microservicos.repositories;

import com.es2.microservicos.domain.CartaoDeCredito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartaoDeCreditoRepository extends JpaRepository<CartaoDeCredito, Long> {
    Optional<CartaoDeCredito> findByCiclistaId(Long idCiclista);
}

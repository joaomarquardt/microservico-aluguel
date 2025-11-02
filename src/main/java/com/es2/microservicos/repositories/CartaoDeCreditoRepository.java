package com.es2.microservicos.repositories;

import com.es2.microservicos.domain.CartaoDeCredito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoDeCreditoRepository extends JpaRepository<CartaoDeCredito, Long> {
    CartaoDeCredito findByCiclistaId(Long idCiclista);
}

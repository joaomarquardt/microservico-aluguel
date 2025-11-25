package com.es2.microservicos.repositories;

import com.es2.microservicos.domain.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AluguelRepository extends JpaRepository<Aluguel, Long> {
    Aluguel findByCiclistaId(Long ciclistaId);

    Optional<Aluguel> findByCiclistaIdAndTrancaFimIsNull(Long ciclistaId);
}

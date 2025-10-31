package com.es2.microservicos.repositories;

import com.es2.microservicos.domain.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AluguelRepository extends JpaRepository<Aluguel, Long> {
}

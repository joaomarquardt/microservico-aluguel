package com.es2.microservicos.repositories;

import com.es2.microservicos.domain.Ciclista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CiclistaRepository extends JpaRepository<Ciclista, Long> {
}

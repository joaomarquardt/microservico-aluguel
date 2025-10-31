package com.es2.microservicos.services;

import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.repositories.CiclistaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CiclistaService {
    private CiclistaRepository ciclistaRepository;

    public CiclistaService(CiclistaRepository ciclistaRepository) {
        this.ciclistaRepository = ciclistaRepository;
    }

    public Ciclista obterCiclistaPorId(Long id) {
        return ciclistaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ciclista não encontrado!"));
    }


}

package com.es2.microservicos.services;

import com.es2.microservicos.repositories.AluguelRepository;
import org.springframework.stereotype.Service;

@Service
public class AluguelService {
    private AluguelRepository aluguelRepository;

    public AluguelService(AluguelRepository aluguelRepository) {
        this.aluguelRepository = aluguelRepository;
    }
}

package com.es2.microservicos.services;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.repositories.AluguelRepository;
import org.springframework.stereotype.Service;

@Service
public class AluguelService {
    private final AluguelRepository aluguelRepository;

    public AluguelService(AluguelRepository aluguelRepository) {
        this.aluguelRepository = aluguelRepository;
    }

    // TODO: Implementar regras de negócio para geração de aluguel
    public Aluguel gerarAluguel() {
        Aluguel aluguel = new Aluguel();
        return aluguelRepository.save(aluguel);
    }
}

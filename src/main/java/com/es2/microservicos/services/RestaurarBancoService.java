package com.es2.microservicos.services;

import com.es2.microservicos.repositories.AluguelRepository;
import com.es2.microservicos.repositories.CartaoDeCreditoRepository;
import com.es2.microservicos.repositories.CiclistaRepository;
import com.es2.microservicos.repositories.FuncionarioRepository;
import org.springframework.stereotype.Service;

@Service
public class RestaurarBancoService {
    private final AluguelRepository aluguelRepository;
    private final CartaoDeCreditoRepository cartaoDeCreditoRepository;
    private final CiclistaRepository ciclistaRepository;
    private final FuncionarioRepository funcionarioRepository;

    public RestaurarBancoService(AluguelRepository aluguelRepository, CartaoDeCreditoRepository cartaoDeCreditoRepository, CiclistaRepository ciclistaRepository, FuncionarioRepository funcionarioRepository) {
        this.aluguelRepository = aluguelRepository;
        this.cartaoDeCreditoRepository = cartaoDeCreditoRepository;
        this.ciclistaRepository = ciclistaRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public void restaurarBanco() {
        aluguelRepository.deleteAll();
        cartaoDeCreditoRepository.deleteAll();
        ciclistaRepository.deleteAll();
        funcionarioRepository.deleteAll();
    }
}

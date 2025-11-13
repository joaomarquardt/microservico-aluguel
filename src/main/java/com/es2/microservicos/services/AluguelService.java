package com.es2.microservicos.services;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.dtos.requests.GerarAluguelRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.dtos.responses.BicicletaResponse;
import com.es2.microservicos.gateways.EquipamentoServiceGateway;
import com.es2.microservicos.mappers.AluguelMapper;
import com.es2.microservicos.repositories.AluguelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AluguelService {
    private final AluguelRepository aluguelRepository;
    private final AluguelMapper aluguelMapper;
    private final EquipamentoServiceGateway equipamentoServiceGateway;
    private final CiclistaService ciclistaService;

    public AluguelService(AluguelRepository aluguelRepository, AluguelMapper aluguelMapper, EquipamentoServiceGateway equipamentoServiceGateway, CiclistaService ciclistaService) {
        this.ciclistaService = ciclistaService;
        this.aluguelMapper = aluguelMapper;
        this.equipamentoServiceGateway = equipamentoServiceGateway;
        this.aluguelRepository = aluguelRepository;
    }

    public AluguelResponse gerarAluguel(GerarAluguelRequest request) {
        if (!equipamentoServiceGateway.existeTrancaPorId(request.trancaInicio())) {
            throw new IllegalArgumentException("Tranca inicial não existe!");
        }
        BicicletaResponse bicicletaTranca = equipamentoServiceGateway.obterBicicletaPorIdTranca(request.trancaInicio());
        Ciclista ciclista = ciclistaService.obterCiclistaPorId(request.ciclistaId());
        Aluguel aluguel = new Aluguel(bicicletaTranca.id(), ciclista, LocalDateTime.now(), null, null, request.trancaInicio(), null);
        // TODO: TERMINAR LÓGICA DE VALIDAÇÃO DO FLUXO PRINCIPAL (UC03)

        Aluguel aluguelSalvo = aluguelRepository.save(aluguel);
        return aluguelMapper.toAluguelResponse(aluguelSalvo);
    }
}

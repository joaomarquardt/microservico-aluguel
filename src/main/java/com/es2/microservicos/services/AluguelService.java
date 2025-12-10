package com.es2.microservicos.services;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.GerarAluguelRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.dtos.responses.BicicletaResponse;
import com.es2.microservicos.external.domain.BicicletaStatus;
import com.es2.microservicos.external.domain.TrancaStatus;
import com.es2.microservicos.external.gateways.EquipamentoServiceGateway;
import com.es2.microservicos.external.gateways.ExternoServiceGateway;
import com.es2.microservicos.mappers.AluguelMapper;
import com.es2.microservicos.repositories.AluguelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AluguelService {
    private final AluguelRepository aluguelRepository;
    private final AluguelMapper aluguelMapper;
    private final EquipamentoServiceGateway equipamentoServiceGateway;
    private final ExternoServiceGateway externoServiceGateway;
    private final CiclistaService ciclistaService;

    public AluguelService(AluguelRepository aluguelRepository, AluguelMapper aluguelMapper, EquipamentoServiceGateway equipamentoServiceGateway, ExternoServiceGateway externoServiceGateway, CiclistaService ciclistaService) {
        this.ciclistaService = ciclistaService;
        this.aluguelMapper = aluguelMapper;
        this.equipamentoServiceGateway = equipamentoServiceGateway;
        this.externoServiceGateway = externoServiceGateway;
        this.aluguelRepository = aluguelRepository;
    }

    public AluguelResponse gerarAluguel(GerarAluguelRequest request) {
        if (!equipamentoServiceGateway.existeTrancaPorId(request.trancaInicio())) {
            throw new IllegalArgumentException("Tranca inicial não existe!");
        }
        BicicletaResponse bicicletaTranca = equipamentoServiceGateway.obterBicicletaPorIdTranca(request.trancaInicio());
        Ciclista ciclista = ciclistaService.obterCiclistaPorId(request.ciclistaId());
        if (ciclista.getStatus() == Status.INATIVO) {
            throw new IllegalArgumentException("Ciclista com status INATIVO não pode alugar bicicleta!");
        }
        Optional<Aluguel> aluguelCiclistaEmAndamento = aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(ciclista.getId());
        if (aluguelCiclistaEmAndamento.isPresent()) {
            AluguelResponse detalhesAluguel = aluguelMapper.toAluguelResponse(aluguelCiclistaEmAndamento.get());
            externoServiceGateway.dadosAluguelAtualEmail(ciclista.getEmail(), detalhesAluguel);
            throw new IllegalArgumentException("O ciclista já possui um aluguel em andamento e não pode alugar outra bicicleta!");
        }
        if (bicicletaTranca != null && (bicicletaTranca.status() == BicicletaStatus.EM_REPARO || bicicletaTranca.status() == BicicletaStatus.REPARO_SOLICITADO)) {
            throw new IllegalArgumentException("A bicicleta solicitada para uso está EM REPARO e não pode ser utilizada!");
        }
        double valorInicialAluguel = Aluguel.valorInicialAluguel();
        externoServiceGateway.cobrarAluguel(valorInicialAluguel, ciclista.getId());
        Aluguel novoAluguel = new Aluguel(bicicletaTranca.id(), ciclista, LocalDateTime.now(), null, valorInicialAluguel, request.trancaInicio(), null);
        Aluguel aluguelSalvo = aluguelRepository.save(novoAluguel);
        equipamentoServiceGateway.alterarStatusBicicleta(novoAluguel.getBicicletaId(), BicicletaStatus.EM_USO);
        equipamentoServiceGateway.alterarStatusTranca(request.trancaInicio(), TrancaStatus.DESTRANCAR);
        AluguelResponse detalhesAluguel = aluguelMapper.toAluguelResponse(novoAluguel);
        externoServiceGateway.dadosAluguelNovoEmail(ciclista.getEmail(), detalhesAluguel);
        return aluguelMapper.toAluguelResponse(aluguelSalvo);
    }

    public Boolean verificarPermissaoAluguel(Long id) {
        return aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(id).isEmpty();
    }

    // TODO: Terminar implementação de método obterBicicletaAlugada conectando com microserviço de Equipamento
    public BicicletaResponse obterBicicletaAlugadaPorIdCiclista(Long ciclistaId) {
        Optional<Aluguel> aluguel = aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(ciclistaId);
        if (aluguel.isEmpty()) {
            throw new EntityNotFoundException("Não existe bicicleta sendo utilizada pelo ciclista com ID: " + ciclistaId);
        }
        equipamentoServiceGateway.obterBicicletaPorId(aluguel.get().getBicicletaId());
        return null;
    }
}

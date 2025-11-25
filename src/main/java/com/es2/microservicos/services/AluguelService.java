package com.es2.microservicos.services;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.GerarAluguelRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.dtos.responses.BicicletaResponse;
import com.es2.microservicos.external.domain.BicicletaStatus;
import com.es2.microservicos.dtos.responses.CartaoResponse;
import com.es2.microservicos.external.domain.TrancaStatus;
import com.es2.microservicos.external.gateways.EquipamentoServiceGateway;
import com.es2.microservicos.external.gateways.ExternoServiceGateway;
import com.es2.microservicos.mappers.AluguelMapper;
import com.es2.microservicos.repositories.AluguelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final CartaoDeCreditoService cartaoDeCreditoService;

    public AluguelService(AluguelRepository aluguelRepository, AluguelMapper aluguelMapper, EquipamentoServiceGateway equipamentoServiceGateway, ExternoServiceGateway externoServiceGateway, CiclistaService ciclistaService, CartaoDeCreditoService cartaoDeCreditoService) {
        this.ciclistaService = ciclistaService;
        this.aluguelMapper = aluguelMapper;
        this.equipamentoServiceGateway = equipamentoServiceGateway;
        this.externoServiceGateway = externoServiceGateway;
        this.aluguelRepository = aluguelRepository;
        this.cartaoDeCreditoService = cartaoDeCreditoService;
    }

    public AluguelResponse gerarAluguel(GerarAluguelRequest request) {
        if (!equipamentoServiceGateway.existeTrancaPorId(request.trancaInicio()).getBody().booleanValue()) {
            throw new IllegalArgumentException("Tranca inicial não existe!");
        }
        ResponseEntity<BicicletaResponse> bicicletaTrancaResponse = equipamentoServiceGateway.obterBicicletaPorIdTranca(request.trancaInicio());
        if (!bicicletaTrancaResponse.hasBody()) {
            throw new IllegalArgumentException("Não existe bicicleta na tranca com ID: " + request.trancaInicio());
        }
        BicicletaResponse bicicletaTrancaBody = bicicletaTrancaResponse.getBody();
        Ciclista ciclista = ciclistaService.obterCiclistaPorId(request.ciclistaId());
        if (ciclista.getStatus() == Status.INATIVO) {
            throw new IllegalArgumentException("Ciclista com status INATIVO não pode alugar bicicleta!");
        }
        Optional<Aluguel> aluguelCiclistaEmAndamento = aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(ciclista.getId());
        if (aluguelCiclistaEmAndamento.isPresent()) {
            AluguelResponse detalhesAluguel = aluguelMapper.toAluguelResponse(aluguelCiclistaEmAndamento.get());
            externoServiceGateway.dadosAluguelAtualEmail(detalhesAluguel);
            throw new IllegalArgumentException("O ciclista já possui um aluguel em andamento e não pode alugar outra bicicleta!");
        }
        if (bicicletaTrancaBody != null && (bicicletaTrancaBody.status() == BicicletaStatus.EM_REPARO || bicicletaTrancaBody.status() == BicicletaStatus.REPARO_SOLICITADO)) {
            throw new IllegalArgumentException("A bicicleta solicitada para uso está EM REPARO e não pode ser utilizada!");
        }
        CartaoResponse cartaoCiclista = cartaoDeCreditoService.obterCartaoDeCreditoPorIdCiclista(ciclista.getId());
        double valorInicialAluguel = Aluguel.valorInicialAluguel();
        ResponseEntity cobrancaAluguelResponse = externoServiceGateway.cobrarAluguel(valorInicialAluguel, cartaoCiclista);
        if (cobrancaAluguelResponse.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException("O pagamento não foi concluído devido a erro no pagamento ou pagamento não autorizado");
        }
        Aluguel novoAluguel = new Aluguel(bicicletaTrancaBody.id(), ciclista, LocalDateTime.now(), null, valorInicialAluguel, request.trancaInicio(), null);
        Aluguel aluguelSalvo = aluguelRepository.save(novoAluguel);
        equipamentoServiceGateway.alterarStatusBicicleta(novoAluguel.getBicicletaId(), BicicletaStatus.EM_USO);
        equipamentoServiceGateway.alterarStatusTranca(request.trancaInicio(), TrancaStatus.DESTRANCAR);
        AluguelResponse detalhesAluguel = aluguelMapper.toAluguelResponse(novoAluguel);
        externoServiceGateway.dadosAluguelNovoEmail(detalhesAluguel);
        return aluguelMapper.toAluguelResponse(aluguelSalvo);
    }

    public Boolean verificarPermissaoAluguel(Long id) {
        return aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(id).isEmpty();
    }
}

package com.es2.microservicos.services;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.dtos.requests.DevolucaoRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.dtos.responses.BicicletaResponse;
import com.es2.microservicos.dtos.responses.CartaoResponse;
import com.es2.microservicos.external.domain.BicicletaStatus;
import com.es2.microservicos.external.domain.TrancaStatus;
import com.es2.microservicos.external.gateways.EquipamentoServiceGateway;
import com.es2.microservicos.external.gateways.ExternoServiceGateway;
import com.es2.microservicos.mappers.AluguelMapper;
import com.es2.microservicos.repositories.AluguelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DevolucaoService {
    private static final double TAXA_POR_MEIA_HORA = 5.0;
    private static final long MINUTOS_INICIAIS = 120;

    private final AluguelRepository aluguelRepository;
    private final AluguelMapper aluguelMapper;
    private final EquipamentoServiceGateway equipamentoServiceGateway;
    private final ExternoServiceGateway externoServiceGateway;
    private final CartaoDeCreditoService cartaoDeCreditoService;

    public DevolucaoService(AluguelRepository aluguelRepository, AluguelMapper aluguelMapper, EquipamentoServiceGateway equipamentoServiceGateway, ExternoServiceGateway externoServiceGateway, CartaoDeCreditoService cartaoDeCreditoService) {
        this.aluguelRepository = aluguelRepository;
        this.aluguelMapper = aluguelMapper;
        this.equipamentoServiceGateway = equipamentoServiceGateway;
        this.externoServiceGateway = externoServiceGateway;
        this.cartaoDeCreditoService = cartaoDeCreditoService;
    }

    @Transactional
    public AluguelResponse devolverBicicleta(DevolucaoRequest request) {
        ResponseEntity<BicicletaResponse> bicicletaResponse = equipamentoServiceGateway.obterBicicletaPorId(request.idBicicleta());
        if (!bicicletaResponse.hasBody() || bicicletaResponse.getBody() == null) {
            throw new IllegalArgumentException("Não existe bicicleta na tranca com ID:" + request.idBicicleta());
        }
        BicicletaResponse bicicleta = bicicletaResponse.getBody();
        if (bicicleta.status() != BicicletaStatus.EM_USO) {
            if (bicicleta.status() == BicicletaStatus.NOVA || bicicleta.status() == BicicletaStatus.EM_REPARO) {
                equipamentoServiceGateway.alterarStatusBicicleta(request.idBicicleta(), BicicletaStatus.DISPONIVEL);
                throw new IllegalArgumentException("Bicicleta com status '" + bicicleta.status() + "'. Redirecionando para integração com totem.");
            }
            throw new IllegalArgumentException("Bicicleta não está em uso!");
        }
        ResponseEntity<Boolean> trancaExiste = equipamentoServiceGateway.existeTrancaPorId(request.idTranca());
        if (trancaExiste.getBody() == null || !trancaExiste.getBody()) {
            throw new IllegalArgumentException("Não existe tranca com ID: " + request.idTranca());
        }
        Optional<Aluguel> aluguelOpt = aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(request.idBicicleta());
        if (aluguelOpt.isEmpty()) {
            throw new IllegalArgumentException("Não existe aluguel em andamento para esta bicicleta!");
        }
        Aluguel aluguel = aluguelOpt.get();
        Ciclista ciclista = aluguel.getCiclista();
        LocalDateTime horaFim = LocalDateTime.now();
        aluguel.setHoraFim(horaFim);
        aluguel.setTrancaFim(request.idTranca());
        Duration duracao = Duration.between(aluguel.getHoraInicio(), horaFim);
        long minutosUsados = duracao.toMinutes();
        double valorExtra = calcularValorExtra(minutosUsados);

        if (minutosUsados > MINUTOS_INICIAIS) {
            CartaoResponse cartao = cartaoDeCreditoService.obterCartaoDeCreditoPorIdCiclista(ciclista.getId());
            ResponseEntity cobrancaResponse = externoServiceGateway.cobrarAluguel(valorExtra, cartao);
            if (cobrancaResponse.getStatusCode() == HttpStatus.OK) {
                aluguel.setCobranca(aluguel.getCobranca() + valorExtra);
            } else {
                // TODO: Implementar registro de cobrança pendente
                aluguel.setCobranca(aluguel.getCobranca() + valorExtra);
            }
        }
        Aluguel aluguelFinalizado = aluguelRepository.save(aluguel);
        equipamentoServiceGateway.alterarStatusBicicleta(request.idBicicleta(), BicicletaStatus.DISPONIVEL);
        equipamentoServiceGateway.alterarStatusTranca(request.idTranca(), TrancaStatus.TRANCAR);
        AluguelResponse aluguelResponse = aluguelMapper.toAluguelResponse(aluguelFinalizado);
        externoServiceGateway.devolucaoBicicletaEmail(
                ciclista.getNome(),
                ciclista.getEmail(),
                aluguelResponse,
                valorExtra
        );
        if (request.requisitarReparo() != null && request.requisitarReparo()) {
            equipamentoServiceGateway.alterarStatusBicicleta(request.idBicicleta(), BicicletaStatus.REPARO_SOLICITADO);
        }
        return aluguelResponse;
    }

    private double calcularValorExtra(long minutosUsados) {
        if (minutosUsados <= MINUTOS_INICIAIS) {
            return 0.0;
        }
        long minutosExtras = minutosUsados - MINUTOS_INICIAIS;
        long meiasHorasExtras = (long) Math.ceil((double) minutosExtras / 30);
        return meiasHorasExtras * TAXA_POR_MEIA_HORA;
    }
}

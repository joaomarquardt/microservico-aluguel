package com.es2.microservicos.external.gateways;

import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.requests.NotificacaoRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.dtos.responses.CartaoResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternoServiceGateway {
    private final RestClient restClient;

    public ExternoServiceGateway(@Qualifier("restClientExterno") RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity confirmacaoCadastroEmail(String nomeDestinario, String emailDestinatario) {
        NotificacaoRequest notificacaoRequest = new NotificacaoRequest(nomeDestinario, emailDestinatario);
        // TODO: Implementar chamada ao endpoint de notificação do microserviço Externo
        return ResponseEntity.ok().build();
    }

    public ResponseEntity atualizacaoCiclistaEmail(String nomeDestinario, String emailDestinatario) {
        NotificacaoRequest notificacaoRequest = new NotificacaoRequest(nomeDestinario, emailDestinatario);
        // TODO: Implementar chamada ao endpoint de notificação do microserviço Externo
        return ResponseEntity.ok().build();
    }

    public ResponseEntity atualizacaoCartaoEmail(String nomeDestinario, String emailDestinatario) {
        NotificacaoRequest notificacaoRequest = new NotificacaoRequest(nomeDestinario, emailDestinatario);
        // TODO: Implementar chamada ao endpoint de notificação do microserviço Externo
        return ResponseEntity.ok().build();
    }

    public ResponseEntity dadosAluguelAtualEmail(AluguelResponse detalhesAluguel) {
        // TODO: Implementar chamada ao endpoint de notificação do microserviço Externo
        return ResponseEntity.ok().build();
    }

    public ResponseEntity dadosAluguelNovoEmail(AluguelResponse detalhesAluguel) {
        // TODO: Implementar chamada ao endpoint de notificação do microserviço Externo
        return ResponseEntity.ok().build();
    }

    public ResponseEntity cobrarAluguel(double valor, CartaoResponse cartaoDetalhes) {
        // TODO: Implementar chamada ao endpoint de cobrança do microserviço Externo
        return ResponseEntity.ok().build();
    }

    public ResponseEntity validacaoCartaoDeCredito(AdicionarCartaoRequest cartaoRequest) {
        // TODO: Implementar chamada ao endpoint de notificação do microserviço Externo
        return ResponseEntity.ok().build();
    }

    public ResponseEntity devolucaoBicicletaEmail(String nomeDestinatario, String emailDestinatario, AluguelResponse aluguelResponse, double taxaExtra) {
        // TODO: Implementar chamada ao endpoint de notificação do microserviço Externo
        return ResponseEntity.ok().build();
    }
}

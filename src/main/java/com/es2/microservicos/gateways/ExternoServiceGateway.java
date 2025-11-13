package com.es2.microservicos.gateways;

import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.requests.NotificacaoRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternoServiceGateway {
    private final RestClient restClient;

    public ExternoServiceGateway(RestClient restClient) {
        this.restClient = RestClient.builder()
                .baseUrl("http://externo-microservico/api") // TODO: Definir a URL base do microserviço Externo
                .build();
    }

    public boolean confirmacaoCadastroEmail(String nomeDestinario, String emailDestinatario) {
        NotificacaoRequest notificacaoRequest = new NotificacaoRequest(nomeDestinario, emailDestinatario);
        // TODO: Implementar chamada ao endpoint de notificação do microserviço Externo
        return true;
    }

    public boolean atualizacaoCiclistaEmail(String nomeDestinario, String emailDestinatario) {
        NotificacaoRequest notificacaoRequest = new NotificacaoRequest(nomeDestinario, emailDestinatario);
        // TODO: Implementar chamada ao endpoint de notificação do microserviço Externo
        return true;
    }

    public boolean atualizacaoCartaoEmail(String nomeDestinario, String emailDestinatario) {
        NotificacaoRequest notificacaoRequest = new NotificacaoRequest(nomeDestinario, emailDestinatario);
        // TODO: Implementar chamada ao endpoint de notificação do microserviço Externo
        return true;
    }

    public boolean validacaoCartaoDeCredito(AdicionarCartaoRequest cartaoRequest) {
        // TODO: Implementar chamada ao endpoint de notificação do microserviço Externo
        return true;
    }


}

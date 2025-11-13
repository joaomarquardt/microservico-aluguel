package com.es2.microservicos.gateways;

import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.requests.NotificacaoRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternoServiceGateway {
    private final RestClient restClient;

    public ExternoServiceGateway(@Qualifier("restClientExterno") RestClient restClient) {
        this.restClient = restClient;
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

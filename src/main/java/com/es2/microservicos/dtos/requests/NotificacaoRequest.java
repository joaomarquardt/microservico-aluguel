package com.es2.microservicos.dtos.requests;

public record NotificacaoRequest(
        String nomeDestinatario,
        String emailDestinatario
) {
}

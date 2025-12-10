package com.es2.microservicos.dtos.requests;

public record EmailRequest(
        String emailDestinatario,
        String assunto,
        String corpoMensagem
) {
}

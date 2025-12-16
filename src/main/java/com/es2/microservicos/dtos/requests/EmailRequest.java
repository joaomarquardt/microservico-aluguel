package com.es2.microservicos.dtos.requests;

public record EmailRequest(
        String email,
        String assunto,
        String mensagem
) {
}

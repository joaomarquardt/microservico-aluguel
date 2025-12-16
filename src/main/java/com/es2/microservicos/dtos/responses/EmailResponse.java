package com.es2.microservicos.dtos.responses;

public record EmailResponse(
        Long id,
        String email,
        String assunto,
        String mensagem
) {
}

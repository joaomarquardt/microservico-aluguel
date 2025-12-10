package com.es2.microservicos.dtos.responses;

public record EmailResponse(
        Long id,
        String emailDestinatario,
        String assunto,
        String corpoMensagem
) {
}

package com.es2.microservicos.dtos.requests;

public record ValidacaoCartaoRequest(
        String nomeTitular,
        String numero,
        String validade,
        String cvv
) {
}

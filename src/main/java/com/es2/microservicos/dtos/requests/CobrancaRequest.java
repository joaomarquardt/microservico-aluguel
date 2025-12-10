package com.es2.microservicos.dtos.requests;

public record CobrancaRequest(
        double valor,
        Long ciclista
) {
}

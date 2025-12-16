package com.es2.microservicos.dtos.requests;

public record GerarAluguelRequest(
        Long ciclista,
        Long trancaInicio
) {
}

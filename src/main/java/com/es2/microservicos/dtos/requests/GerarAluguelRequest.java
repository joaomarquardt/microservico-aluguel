package com.es2.microservicos.dtos.requests;

public record GerarAluguelRequest(
        Long ciclistaId,
        Long trancaInicio
) {
}

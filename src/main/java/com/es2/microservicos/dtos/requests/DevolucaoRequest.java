package com.es2.microservicos.dtos.requests;

public record DevolucaoRequest(
        Long idTranca,
        Long idBicicleta,
        Boolean requisitarReparo
) {
}


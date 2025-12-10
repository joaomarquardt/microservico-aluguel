package com.es2.microservicos.dtos.responses;

public record CobrancaResponse(
        Long id,
        String status,
        String horaSolicitacao,
        String horaFinalizacao,
        double valor,
        Long ciclistaId
) {
}

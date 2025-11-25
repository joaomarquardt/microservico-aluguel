package com.es2.microservicos.dtos.responses;

import com.es2.microservicos.external.domain.BicicletaStatus;

public record BicicletaResponse(
        Long id,
        String marca,
        String modelo,
        String ano,
        int numero,
        BicicletaStatus status
) {
}

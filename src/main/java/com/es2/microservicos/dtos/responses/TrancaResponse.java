package com.es2.microservicos.dtos.responses;

public record TrancaResponse(
        Long id,
        Long bicicletaId,
        int numero,
        String localizacao,
        String anoDeFabricacao,
        String modelo,
        String status
) {
}

package com.es2.microservicos.dtos.responses;

public record BicicletaResponse(
        Long id,
        String marca,
        String modelo,
        String ano,
        int numero,
        String status
) {
}

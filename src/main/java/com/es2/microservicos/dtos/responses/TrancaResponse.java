package com.es2.microservicos.dtos.responses;

import java.time.LocalDate;

public record TrancaResponse(
        Long id,
        Long bicicletaId,
        int numero,
        String localizacao,
        LocalDate anoDeFabricacao,
        String modelo,
        String status
) {
}

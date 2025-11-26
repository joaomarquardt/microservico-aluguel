package com.es2.microservicos.dtos.responses;

import java.time.LocalDate;

public record PassaporteResponse(
        Long id,
        String numero,
        LocalDate validade,
        String pais
) {
}

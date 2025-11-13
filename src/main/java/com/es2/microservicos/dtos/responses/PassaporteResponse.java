package com.es2.microservicos.dtos.responses;

import java.time.LocalDate;

public record PassaporteResponse(
        String numero,
        LocalDate validade,
        String pais
) {
}

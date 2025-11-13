package com.es2.microservicos.dtos.requests;

import java.time.LocalDate;

public record RegistrarPassaporteRequest(
        String numero,
        LocalDate validade,
        String pais
) {
}

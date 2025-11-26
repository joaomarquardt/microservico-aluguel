package com.es2.microservicos.dtos.responses;

import java.time.LocalDate;

public record CartaoResponse(
        Long id,
        String nomeTitular,
        String numeroCartao,
        LocalDate dataValidade,
        String codigoSeguranca
) {
}

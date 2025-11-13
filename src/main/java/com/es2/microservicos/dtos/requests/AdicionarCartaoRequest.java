package com.es2.microservicos.dtos.requests;

import java.time.LocalDate;

public record AdicionarCartaoRequest(
        String nomeTitular,
        String numeroCartao,
        LocalDate dataValidade,
        String codigoSeguranca
) {
}

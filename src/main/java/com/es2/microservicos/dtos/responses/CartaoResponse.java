package com.es2.microservicos.dtos.responses;

public record CartaoResponse(
        String nomeTitular,
        String numeroCartao,
        String dataValidade,
        String codigoSeguranca
) {
}

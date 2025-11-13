package com.es2.microservicos.dtos.requests;

public record AdicionarCartaoRequest(
        String nomeTitular,
        String numeroCartao,
        String dataValidade,
        String codigoSeguranca
) {
}

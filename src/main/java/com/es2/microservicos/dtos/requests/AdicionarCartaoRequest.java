package com.es2.microservicos.dtos.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AdicionarCartaoRequest(
        @NotEmpty(message = "Nome do titular não pode ser vazio!")
        String nomeTitular,
        @NotEmpty(message = "Número do cartão não pode ser vazio!")
        String numeroCartao,
        @NotNull(message = "Data de validade não pode ser nula!")
        @Future(message = "Data de validade deve ser uma data futura!")
        LocalDate dataValidade,
        @NotEmpty(message = "Código de segurança não pode ser vazio!")
        String codigoSeguranca
) {
}

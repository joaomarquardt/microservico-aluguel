package com.es2.microservicos.dtos.requests;

import com.es2.microservicos.domain.Nacionalidade;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CriarCiclistaRequest(
        @NotEmpty(message = "O nome do ciclista não pode ser vazio!")
        String nome,
        @NotNull(message = "A nacionalidade do ciclista não pode ser nula!")
        Nacionalidade nacionalidade,
        @NotNull(message = "A data de nascimento do ciclista não pode ser nula!")
        @Past(message = "A data de nascimento deve ser uma data passada!")
        LocalDate nascimento,
        @Pattern(regexp = "^\\d{11}$", message = "O CPF deve conter exatamente 11 dígitos numéricos!")
        String cpf,
        @Email(message = "O email deve ser válido!")
        String email,
        @NotEmpty(message = "A senha do ciclista não pode ser vazia!")
        String senha,
        @NotEmpty(message = "A confirmação de senha do ciclista não pode ser vazia!")
        String confirmacaoSenha,
        @NotNull(message = "O passaporte do ciclista não pode ser nulo!")
        RegistrarPassaporteRequest passaporte,
        @NotNull(message = "O cartão de crédito do ciclista não pode ser nulo!")
        AdicionarCartaoRequest cartaoDeCredito,
        @NotEmpty(message = "A URL da foto do documento não pode ser vazia!")
        String urlFotoDocumento
) {
}

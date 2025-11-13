package com.es2.microservicos.dtos.requests;

import com.es2.microservicos.domain.Nacionalidade;

import java.time.LocalDate;

public record AtualizarCiclistaRequest(
        String nome,
        Nacionalidade nacionalidade,
        LocalDate nascimento,
        String cpf,
        String email,
        String senha,
        String confirmacaoSenha,
        RegistrarPassaporteRequest passaporte,
        String urlFotoDocumento
) {
}

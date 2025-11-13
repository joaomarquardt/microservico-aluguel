package com.es2.microservicos.dtos.requests;

import com.es2.microservicos.domain.Nacionalidade;

public record CriarCiclistaRequest(
        String nome,
        Nacionalidade nacionalidade,
        String cpf,
        String email,
        String senha,
        String confirmacaoSenha,
        RegistrarPassaporteRequest passaporte,
        AdicionarCartaoRequest cartaoDeCredito,
        String urlFotoDocumento
) {
}

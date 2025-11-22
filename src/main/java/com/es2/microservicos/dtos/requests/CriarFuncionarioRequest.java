package com.es2.microservicos.dtos.requests;

import com.es2.microservicos.domain.Funcao;

public record CriarFuncionarioRequest(
        String nome,
        String email,
        String senha,
        String confirmacaoSenha,
        String cpf,
        Integer idade,
        Funcao funcao
) {
}

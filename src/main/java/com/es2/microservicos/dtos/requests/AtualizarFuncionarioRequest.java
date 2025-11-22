package com.es2.microservicos.dtos.requests;

import com.es2.microservicos.domain.Funcao;

public record AtualizarFuncionarioRequest(
        String nome,
        String email,
        String senha,
        String confirmacaoSenha,
        Integer idade,
        Funcao funcao
) {
}

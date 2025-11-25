package com.es2.microservicos.dtos.responses;

import com.es2.microservicos.domain.Funcao;

public record FuncionarioResponse(
        String matricula,
        String nome,
        String email,
        String senha,
        String confirmacaoSenha,
        String cpf,
        Integer idade,
        Funcao funcao
) {
}

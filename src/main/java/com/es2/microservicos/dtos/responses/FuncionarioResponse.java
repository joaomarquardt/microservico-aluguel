package com.es2.microservicos.dtos.responses;

public record FuncionarioResponse(
        String matricula,
        String nome,
        String email,
        String senha,
        String confirmacaoSenha,
        String cpf,
        Integer idade,
        String funcao
) {
}

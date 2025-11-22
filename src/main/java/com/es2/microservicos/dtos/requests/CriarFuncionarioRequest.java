package com.es2.microservicos.dtos.requests;

public record CriarFuncionarioRequest(
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

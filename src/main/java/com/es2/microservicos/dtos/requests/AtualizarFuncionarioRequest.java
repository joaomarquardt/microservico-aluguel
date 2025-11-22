package com.es2.microservicos.dtos.requests;

public record AtualizarFuncionarioRequest(
        String nome,
        String email,
        String senha,
        String confirmacaoSenha,
        String cpf,
        Integer idade,
        Funcao funcao
) {
}

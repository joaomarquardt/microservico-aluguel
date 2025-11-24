package com.es2.microservicos.dtos.requests;

import com.es2.microservicos.domain.Funcao;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AtualizarFuncionarioRequest(
        @NotEmpty(message = "O nome do funcionário não pode ser vazio!")
        String nome,
        @Email(message = "O email deve ser válido!")
        String email,
        @NotEmpty(message = "A senha do funcionário não pode ser vazia!")
        String senha,
        @NotEmpty(message = "A confirmação de senha do funcionário não pode ser vazia!")
        String confirmacaoSenha,
        @Min(value = 0, message = "A idade não pode ser negativa!")
        Integer idade,
        @NotNull(message = "A função do funcionário não pode ser nula!")
        Funcao funcao
) {
}

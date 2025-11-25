package com.es2.microservicos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para Funcionario")
class FuncionarioTest {

    @Test
    @DisplayName("Deve criar funcionário com construtor vazio")
    void deveCriarFuncionarioComConstrutorVazio() {
        Funcionario funcionario = new Funcionario();
        assertNotNull(funcionario);
    }

    @Test
    @DisplayName("Deve criar funcionário com construtor completo")
    void deveCriarFuncionarioComConstrutorCompleto() {
        Funcionario funcionario = new Funcionario(
                1L,
                "MAT123",
                "Carlos Silva",
                "carlos@empresa.com",
                "senha123",
                "senha123",
                "12345678901",
                30,
                Funcao.REPARADOR
        );

        assertEquals(1L, funcionario.getId());
        assertEquals("MAT123", funcionario.getMatricula());
        assertEquals("Carlos Silva", funcionario.getNome());
        assertEquals("carlos@empresa.com", funcionario.getEmail());
        assertEquals("senha123", funcionario.getSenha());
        assertEquals("senha123", funcionario.getConfirmacaoSenha());
        assertEquals("12345678901", funcionario.getCpf());
        assertEquals(30, funcionario.getIdade());
        assertEquals(Funcao.REPARADOR.toString(), funcionario.getFuncao().toString());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos")
    void deveDefinirEObterTodosCampos() {
        Funcionario funcionario = new Funcionario();

        funcionario.setId(1L);
        funcionario.setMatricula("MAT123");
        funcionario.setNome("Carlos Silva");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenha("senha123");
        funcionario.setConfirmacaoSenha("senha123");
        funcionario.setCpf("12345678901");
        funcionario.setIdade(30);
        funcionario.setFuncao(Funcao.REPARADOR);

        assertEquals(1L, funcionario.getId());
        assertEquals("MAT123", funcionario.getMatricula());
        assertEquals("Carlos Silva", funcionario.getNome());
        assertEquals("carlos@empresa.com", funcionario.getEmail());
        assertEquals("senha123", funcionario.getSenha());
        assertEquals("senha123", funcionario.getConfirmacaoSenha());
        assertEquals("12345678901", funcionario.getCpf());
        assertEquals(30, funcionario.getIdade());
        assertEquals(Funcao.REPARADOR.toString(), funcionario.getFuncao().toString());
    }
}

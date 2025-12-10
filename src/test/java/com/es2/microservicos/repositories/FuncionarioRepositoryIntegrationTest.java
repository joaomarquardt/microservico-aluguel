package com.es2.microservicos.repositories;

import com.es2.microservicos.domain.Funcao;
import com.es2.microservicos.domain.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes de Integração - FuncionarioRepository")
class FuncionarioRepositoryIntegrationTest {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Funcionario funcionario;

    @BeforeEach
    void setUp() {
        funcionario = new Funcionario();
        funcionario.setNome("Maria Silva");
        funcionario.setEmail("maria@empresa.com");
        funcionario.setCpf("98765432100");
        funcionario.setSenha("senha123");
        funcionario.setIdade(30);
        funcionario.setFuncao(Funcao.REPARADOR);
    }

    @Test
    @DisplayName("Deve salvar funcionário no banco de dados")
    void deveSalvarFuncionarioNoBancoDeDados() {
        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
        assertThat(funcionarioSalvo.getId()).isNotNull();
        assertThat(funcionarioSalvo.getNome()).isEqualTo("Maria Silva");
        assertThat(funcionarioSalvo.getEmail()).isEqualTo("maria@empresa.com");
    }

    @Test
    @DisplayName("Deve buscar funcionário por ID")
    void deveBuscarFuncionarioPorId() {
        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
        entityManager.flush();
        entityManager.clear();
        Optional<Funcionario> resultado = funcionarioRepository.findById(funcionarioSalvo.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Maria Silva");
        assertThat(resultado.get().getCpf()).isEqualTo("98765432100");
    }

    @Test
    @DisplayName("Deve verificar se email já existe")
    void deveVerificarSeEmailJaExiste() {
        funcionarioRepository.save(funcionario);
        entityManager.flush();
        boolean existe = funcionarioRepository.existsByEmail("maria@empresa.com");
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando email não existe")
    void deveRetornarFalseQuandoEmailNaoExiste() {
        boolean existe = funcionarioRepository.existsByEmail("naoexiste@empresa.com");
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve atualizar informações do funcionário")
    void deveAtualizarInformacoesDeFuncionario() {
        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
        entityManager.flush();
        funcionarioSalvo.setNome("Maria Silva Santos");
        funcionarioSalvo.setFuncao(Funcao.REPARADOR);
        funcionarioSalvo.setIdade(31);
        funcionarioRepository.save(funcionarioSalvo);
        entityManager.flush();
        entityManager.clear();
        Funcionario funcionarioAtualizado = funcionarioRepository.findById(funcionarioSalvo.getId()).orElseThrow();
        assertThat(funcionarioAtualizado.getNome()).isEqualTo("Maria Silva Santos");
        assertThat(funcionarioAtualizado.getFuncao()).isEqualTo(Funcao.REPARADOR);
        assertThat(funcionarioAtualizado.getIdade()).isEqualTo(31);
    }

    @Test
    @DisplayName("Deve deletar funcionário do banco de dados")
    void deveDeletarFuncionarioDoBancoDeDados() {
        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
        entityManager.flush();
        Long funcionarioId = funcionarioSalvo.getId();
        funcionarioRepository.delete(funcionarioSalvo);
        entityManager.flush();
        Optional<Funcionario> resultado = funcionarioRepository.findById(funcionarioId);
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar funcionário inexistente")
    void deveRetornarVazioAoBuscarFuncionarioInexistente() {
        Optional<Funcionario> resultado = funcionarioRepository.findById(999L);
        assertThat(resultado).isEmpty();
    }
}

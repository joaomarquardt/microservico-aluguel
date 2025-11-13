package com.es2.microservicos.services;

import com.es2.microservicos.domain.Funcionario;
import com.es2.microservicos.repositories.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Testes unitários para FuncionarioService")
@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private Funcionario funcionario;

    @BeforeEach
    void setUp() {
        funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setMatricula("1234");
        funcionario.setNome("João Silva");
        funcionario.setEmail("joao@email.com");
        funcionario.setSenha("senha123");
        funcionario.setConfirmacaoSenha("senha123");
        funcionario.setCpf("12345678901");
        funcionario.setIdade(30);
        funcionario.setFuncao("Gerente");
    }

    @Test
    @DisplayName("Deve listar funcionários")
    void deveListarFuncionarios() {
        List<Funcionario> lista = Arrays.asList(funcionario);
        when(funcionarioRepository.findAll()).thenReturn(lista);

        List<Funcionario> resultado = funcionarioService.listarFuncionarios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
        verify(funcionarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar funcionário por ID")
    void deveRetornarFuncionarioPorId() {
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));

        Funcionario resultado = funcionarioService.obterFuncionarioPorId(1L);

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        verify(funcionarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção se funcionário não existe ao buscar por ID")
    void deveLancarExcecaoFuncionarioNaoEncontrado() {
        when(funcionarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> funcionarioService.obterFuncionarioPorId(99L));

        verify(funcionarioRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve criar funcionário corretamente")
    void deveCriarFuncionario() {
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(funcionario);

        Funcionario novo = new Funcionario();
        novo.setNome("Novo Func");
        Funcionario resultado = funcionarioService.criarFuncionario(novo);

        assertNotNull(resultado);
        verify(funcionarioRepository, times(1)).save(novo);
    }

    @Test
    @DisplayName("Deve atualizar dados do funcionário existente")
    void deveAtualizarFuncionario() {
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(funcionario);

        Funcionario detalhes = new Funcionario();
        detalhes.setMatricula("5678");
        detalhes.setNome("João Atualizado");
        detalhes.setEmail("atual@email.com");
        detalhes.setSenha("novaSenha");
        detalhes.setConfirmacaoSenha("novaSenha");
        detalhes.setCpf("98765432100");
        detalhes.setIdade(32);
        detalhes.setFuncao("Diretor");

        Funcionario atualizado = funcionarioService.atualizarFuncionario(1L, detalhes);

        assertNotNull(atualizado);
        assertEquals("João Atualizado", atualizado.getNome());
        assertEquals("5678", atualizado.getMatricula());
        assertEquals("atual@email.com", atualizado.getEmail());
        assertEquals("Diretor", atualizado.getFuncao());
        verify(funcionarioRepository, times(1)).findById(1L);
        verify(funcionarioRepository, times(1)).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se tentar atualizar funcionário inexistente")
    void deveLancarExcecaoAoAtualizarFuncionarioInexistente() {
        when(funcionarioRepository.findById(2L)).thenReturn(Optional.empty());
        Funcionario novo = new Funcionario();
        assertThrows(EntityNotFoundException.class,
                () -> funcionarioService.atualizarFuncionario(2L, novo));
        verify(funcionarioRepository, times(1)).findById(2L);
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve deletar funcionário existente")
    void deveDeletarFuncionario() {
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        doNothing().when(funcionarioRepository).delete(funcionario);

        assertDoesNotThrow(() -> funcionarioService.deletarFuncionario(1L));
        verify(funcionarioRepository, times(1)).findById(1L);
        verify(funcionarioRepository, times(1)).delete(funcionario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar funcionário inexistente")
    void deveLancarExcecaoAoDeletarFuncionarioInexistente() {
        when(funcionarioRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> funcionarioService.deletarFuncionario(77L));

        verify(funcionarioRepository, times(1)).findById(77L);
        verify(funcionarioRepository, never()).delete(any(Funcionario.class));
    }
}

package com.es2.microservicos.services;

import com.es2.microservicos.domain.Funcao;
import com.es2.microservicos.domain.Funcionario;
import com.es2.microservicos.dtos.requests.AtualizarFuncionarioRequest;
import com.es2.microservicos.dtos.requests.CriarFuncionarioRequest;
import com.es2.microservicos.dtos.responses.FuncionarioResponse;
import com.es2.microservicos.mappers.FuncionarioMapper;
import com.es2.microservicos.repositories.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para FuncionarioService")
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private FuncionarioMapper funcionarioMapper;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private Funcionario funcionario;
    private CriarFuncionarioRequest criarRequest;
    private AtualizarFuncionarioRequest atualizarRequest;
    private FuncionarioResponse funcionarioResponse;

    @BeforeEach
    void setUp() {
        funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setMatricula("FUNC00001");
        funcionario.setNome("Carlos Silva");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenha("senha123");
        funcionario.setConfirmacaoSenha("senha123");
        funcionario.setCpf("12345678901");
        funcionario.setIdade(30);
        funcionario.setFuncao(Funcao.REPARADOR);

        criarRequest = new CriarFuncionarioRequest(
                "Carlos Silva",
                "carlos@empresa.com",
                "senha123",
                "senha123",
                "12345678901",
                30,
                Funcao.REPARADOR
        );

        atualizarRequest = new AtualizarFuncionarioRequest(
                "Carlos Silva Atualizado",
                "carlos.atualizado@empresa.com",
                "senha123",
                "senha123",
                35,
                Funcao.ADMINISTRATIVO
        );

        funcionarioResponse = new FuncionarioResponse(
                "FUNC00001",
                "Carlos Silva",
                "carlos@empresa.com",
                "senha123",
                "senha123",
                "12345678901",
                30,
                Funcao.REPARADOR
        );
    }

    @Test
    @DisplayName("Deve listar todos os funcionários")
    void deveListarTodosFuncionarios() {
        List<Funcionario> funcionarios = Arrays.asList(funcionario);
        List<FuncionarioResponse> responses = Arrays.asList(funcionarioResponse);

        when(funcionarioRepository.findAll()).thenReturn(funcionarios);
        when(funcionarioMapper.toFuncionarioResponseList(funcionarios)).thenReturn(responses);

        List<FuncionarioResponse> resultado = funcionarioService.listarFuncionarios();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Carlos Silva", resultado.get(0).nome());
        verify(funcionarioRepository, times(1)).findAll();
        verify(funcionarioMapper, times(1)).toFuncionarioResponseList(funcionarios);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há funcionários")
    void deveRetornarListaVaziaQuandoNaoHaFuncionarios() {
        when(funcionarioRepository.findAll()).thenReturn(Collections.emptyList());
        when(funcionarioMapper.toFuncionarioResponseList(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        List<FuncionarioResponse> resultado = funcionarioService.listarFuncionarios();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(funcionarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve obter funcionário por ID com sucesso")
    void deveObterFuncionarioPorIdComSucesso() {
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioMapper.toFuncionarioResponse(funcionario)).thenReturn(funcionarioResponse);

        FuncionarioResponse resultado = funcionarioService.obterFuncionarioPorId(1L);

        assertNotNull(resultado);
        assertEquals("Carlos Silva", resultado.nome());
        assertEquals("carlos@empresa.com", resultado.email());
        assertEquals(Funcao.REPARADOR, resultado.funcao());
        verify(funcionarioRepository, times(1)).findById(1L);
        verify(funcionarioMapper, times(1)).toFuncionarioResponse(funcionario);
    }

    @Test
    @DisplayName("Deve lançar exceção quando funcionário não encontrado")
    void deveLancarExcecaoQuandoFuncionarioNaoEncontrado() {
        when(funcionarioRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> funcionarioService.obterFuncionarioPorId(99L)
        );

        assertEquals("Funcionário não encontrado!", exception.getMessage());
        verify(funcionarioRepository, times(1)).findById(99L);
        verify(funcionarioMapper, never()).toFuncionarioResponse(any());
    }

    @Test
    @DisplayName("Deve obter entidade de funcionário por ID")
    void deveObterEntidadeFuncionarioPorId() {
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));

        Funcionario resultado = funcionarioService.obterEntidadeFuncionarioPorId(1L);

        assertNotNull(resultado);
        assertEquals(funcionario.getId(), resultado.getId());
        assertEquals(funcionario.getNome(), resultado.getNome());
        verify(funcionarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve criar funcionário com sucesso")
    void deveCriarFuncionarioComSucesso() {
        when(funcionarioMapper.toFuncionario(criarRequest)).thenReturn(funcionario);
        when(funcionarioRepository.count()).thenReturn(0L);
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(funcionario);
        when(funcionarioMapper.toFuncionarioResponse(funcionario)).thenReturn(funcionarioResponse);

        FuncionarioResponse resultado = funcionarioService.criarFuncionario(criarRequest);

        assertNotNull(resultado);
        assertEquals("Carlos Silva", resultado.nome());
        assertEquals("carlos@empresa.com", resultado.email());
        verify(funcionarioMapper, times(1)).toFuncionario(criarRequest);
        verify(funcionarioRepository, times(1)).save(any(Funcionario.class));
        verify(funcionarioMapper, times(1)).toFuncionarioResponse(funcionario);
    }

    @Test
    @DisplayName("Deve gerar matrícula corretamente ao criar funcionário")
    void deveGerarMatriculaCorretamenteAoCriarFuncionario() {
        when(funcionarioMapper.toFuncionario(criarRequest)).thenReturn(funcionario);
        when(funcionarioRepository.count()).thenReturn(5L);
        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(invocation -> {
            Funcionario func = invocation.getArgument(0);
            assertEquals("FUNC00006", func.getMatricula());
            return func;
        });
        when(funcionarioMapper.toFuncionarioResponse(any())).thenReturn(funcionarioResponse);

        funcionarioService.criarFuncionario(criarRequest);

        verify(funcionarioRepository, times(1)).count();
        verify(funcionarioRepository, times(1)).save(argThat(func ->
                func.getMatricula().equals("FUNC00006")
        ));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar funcionário com senhas diferentes")
    void deveLancarExcecaoAoCriarFuncionarioComSenhasDiferentes() {
        CriarFuncionarioRequest requestInvalido = new CriarFuncionarioRequest(
                "Carlos Silva",
                "carlos@empresa.com",
                "senha123",
                "senha456",
                "12345678901",
                30,
                Funcao.REPARADOR
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> funcionarioService.criarFuncionario(requestInvalido)
        );

        assertEquals("Senha e confirmação de senha não coincidem!", exception.getMessage());
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar funcionário com sucesso")
    void deveAtualizarFuncionarioComSucesso() {
        Funcionario funcionarioAtualizado = new Funcionario();
        funcionarioAtualizado.setId(1L);
        funcionarioAtualizado.setMatricula("FUNC00001");
        funcionarioAtualizado.setNome("Carlos Silva Atualizado");
        funcionarioAtualizado.setEmail("carlos.atualizado@empresa.com");
        funcionarioAtualizado.setIdade(35);
        funcionarioAtualizado.setFuncao(Funcao.ADMINISTRATIVO);

        FuncionarioResponse responseAtualizado = new FuncionarioResponse(
                "FUNC00001",
                "Carlos Silva Atualizado",
                "carlos.atualizado@empresa.com",
                "senha123",
                "senha123",
                "12345678901",
                35,
                Funcao.ADMINISTRATIVO
        );

        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        doNothing().when(funcionarioMapper).updateFuncionarioFromRequest(atualizarRequest, funcionario);
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionarioAtualizado);
        when(funcionarioMapper.toFuncionarioResponse(funcionarioAtualizado)).thenReturn(responseAtualizado);

        FuncionarioResponse resultado = funcionarioService.atualizarFuncionario(1L, atualizarRequest);

        assertNotNull(resultado);
        assertEquals("Carlos Silva Atualizado", resultado.nome());
        assertEquals("carlos.atualizado@empresa.com", resultado.email());
        assertEquals(35, resultado.idade());
        assertEquals(Funcao.ADMINISTRATIVO, resultado.funcao());
        verify(funcionarioRepository, times(1)).findById(1L);
        verify(funcionarioMapper, times(1)).updateFuncionarioFromRequest(atualizarRequest, funcionario);
        verify(funcionarioRepository, times(1)).save(funcionario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com senhas diferentes")
    void deveLancarExcecaoAoAtualizarComSenhasDiferentes() {
        AtualizarFuncionarioRequest requestInvalido = new AtualizarFuncionarioRequest(
                "Carlos Silva",
                "carlos@empresa.com",
                "senha123",
                "senha456",
                30,
                Funcao.REPARADOR
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> funcionarioService.atualizarFuncionario(1L, requestInvalido)
        );

        assertEquals("Senha e confirmação de senha não coincidem!", exception.getMessage());
        verify(funcionarioRepository, never()).findById(anyLong());
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar funcionário inexistente")
    void deveLancarExcecaoAoAtualizarFuncionarioInexistente() {
        when(funcionarioRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> funcionarioService.atualizarFuncionario(2L, atualizarRequest)
        );

        assertEquals("Funcionário não encontrado!", exception.getMessage());
        verify(funcionarioRepository, times(1)).findById(2L);
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar funcionário com sucesso")
    void deveDeletarFuncionarioComSucesso() {
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

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> funcionarioService.deletarFuncionario(77L)
        );

        assertEquals("Funcionário não encontrado!", exception.getMessage());
        verify(funcionarioRepository, times(1)).findById(77L);
        verify(funcionarioRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve criar funcionário com função ADMINISTRATIVO")
    void deveCriarFuncionarioComFuncaoAdministrativo() {
        CriarFuncionarioRequest requestAdmin = new CriarFuncionarioRequest(
                "Maria Admin",
                "maria@empresa.com",
                "senha123",
                "senha123",
                "98765432100",
                28,
                Funcao.ADMINISTRATIVO
        );

        Funcionario funcionarioAdmin = new Funcionario();
        funcionarioAdmin.setFuncao(Funcao.ADMINISTRATIVO);

        FuncionarioResponse responseAdmin = new FuncionarioResponse(
                "FUNC00002", "Maria Admin", "maria@empresa.com", "senha123", "senha123", "98765432100", 28, Funcao.ADMINISTRATIVO
        );

        when(funcionarioMapper.toFuncionario(requestAdmin)).thenReturn(funcionarioAdmin);
        when(funcionarioRepository.count()).thenReturn(1L);
        when(funcionarioRepository.save(any())).thenReturn(funcionarioAdmin);
        when(funcionarioMapper.toFuncionarioResponse(any())).thenReturn(responseAdmin);

        FuncionarioResponse resultado = funcionarioService.criarFuncionario(requestAdmin);

        assertEquals(Funcao.ADMINISTRATIVO, resultado.funcao());
        verify(funcionarioRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve gerar matrículas sequenciais para múltiplos funcionários")
    void deveGerarMatriculasSequenciaisParaMultiplosFuncionarios() {
        when(funcionarioMapper.toFuncionario(any())).thenReturn(funcionario);
        when(funcionarioRepository.count()).thenReturn(0L, 1L, 2L);
        when(funcionarioRepository.save(any())).thenReturn(funcionario);
        when(funcionarioMapper.toFuncionarioResponse(any())).thenReturn(funcionarioResponse);

        funcionarioService.criarFuncionario(criarRequest);
        funcionarioService.criarFuncionario(criarRequest);
        funcionarioService.criarFuncionario(criarRequest);

        verify(funcionarioRepository, times(3)).count();
        verify(funcionarioRepository, times(3)).save(any());
    }
}

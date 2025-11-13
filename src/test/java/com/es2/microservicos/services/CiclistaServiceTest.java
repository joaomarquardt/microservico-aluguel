package com.es2.microservicos.services;

import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Nacionalidade;
import com.es2.microservicos.domain.Passaporte;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.AtualizarCiclistaRequest;
import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.requests.CriarCiclistaRequest;
import com.es2.microservicos.dtos.requests.RegistrarPassaporteRequest;
import com.es2.microservicos.dtos.responses.CiclistaResponse;
import com.es2.microservicos.gateways.EquipamentoServiceGateway;
import com.es2.microservicos.gateways.ExternoServiceGateway;
import com.es2.microservicos.mappers.CiclistaMapper;
import com.es2.microservicos.repositories.CiclistaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CiclistaService")
class CiclistaServiceTest {

    @Mock
    private CiclistaRepository ciclistaRepository;

    @Mock
    private CiclistaMapper ciclistaMapper;

    @Mock
    private CartaoDeCreditoService cartaoService;

    @Mock
    private AluguelService aluguelService;

    @Mock
    private ExternoServiceGateway externoServiceGateway;

    @Mock
    private EquipamentoServiceGateway equipamentoServiceGateway;

    @InjectMocks
    private CiclistaService ciclistaService;

    private Ciclista ciclista;
    private CriarCiclistaRequest criarCiclistaRequest;
    private AtualizarCiclistaRequest atualizarCiclistaRequest;
    private CiclistaResponse ciclistaResponse;
    private AdicionarCartaoRequest cartaoRequest;
    private RegistrarPassaporteRequest passaporteRequest;

    @BeforeEach
    void setUp() {
        cartaoRequest = new AdicionarCartaoRequest(
                "João Silva",
                "1234567890123456",
                LocalDate.of(2027, 12, 31),
                "123"
        );

        passaporteRequest = new RegistrarPassaporteRequest(
                "João Silva",
                LocalDate.of(2028, 5, 20),
                "BR"
        );

        ciclista = new Ciclista(
                1L,
                "João Silva",
                "joao@email.com",
                Status.INATIVO,
                "12345678901",
                LocalDate.of(1990, 1, 1),
                Nacionalidade.BRASILEIRO,
                null,
                "http://foto.com/doc.jpg"
        );

        criarCiclistaRequest = new CriarCiclistaRequest(
                "João Silva",
                Nacionalidade.BRASILEIRO,
                LocalDate.of(1990, 1, 1),
                "12345678901",
                "joao@email.com",
                "senha123",
                "senha123",
                passaporteRequest,
                cartaoRequest,
                "http://foto.com/doc.jpg"

        );

        atualizarCiclistaRequest = new AtualizarCiclistaRequest(
                "João Silva Atualizado",
                Nacionalidade.BRASILEIRO,
                LocalDate.of(1990, 1, 1),
                "12345678901",
                "joao@email.com",
                "senha123",
                "senha123",
                passaporteRequest,
                "http://foto.com/doc.jpg"
        );

        ciclistaResponse = new CiclistaResponse(
                1L,
                Status.INATIVO,
                "João Silva",
                LocalDate.of(1990, 1, 1),
                "12345678901",
                null,
                Nacionalidade.BRASILEIRO.toString(),
                "joao@email.com",
                "http://foto.com/doc.jpg"

        );
    }

    @Test
    @DisplayName("Deve obter ciclista por ID com sucesso")
    void deveObterCiclistaPorIdComSucesso() {
        when(ciclistaRepository.findById(1L)).thenReturn(Optional.of(ciclista));

        Ciclista resultado = ciclistaService.obterCiclistaPorId(1L);

        assertNotNull(resultado);
        assertEquals(ciclista.getId(), resultado.getId());
        assertEquals(ciclista.getNome(), resultado.getNome());
        verify(ciclistaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ciclista não for encontrado")
    void deveLancarExcecaoQuandoCiclistaNaoEncontrado() {
        when(ciclistaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> ciclistaService.obterCiclistaPorId(1L));
        verify(ciclistaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve criar ciclista brasileiro com sucesso")
    void deveCriarCiclistaBrasileiroComSucesso() {
        when(ciclistaRepository.existsByEmail(anyString())).thenReturn(false);
        when(ciclistaMapper.toCiclista(any(CriarCiclistaRequest.class))).thenReturn(ciclista);
        when(ciclistaRepository.save(any(Ciclista.class))).thenReturn(ciclista);
        when(ciclistaMapper.toCiclistaResponse(any(Ciclista.class))).thenReturn(ciclistaResponse);
        when(externoServiceGateway.validacaoCartaoDeCredito(any())).thenReturn(true);
        when(cartaoService.cadastrarCartaoDeCredito(any(), any(Ciclista.class))).thenReturn(null);
        when(externoServiceGateway.confirmacaoCadastroEmail(anyString(), anyString())).thenReturn(true);

        CiclistaResponse resultado = ciclistaService.criarCiclista(criarCiclistaRequest);

        assertNotNull(resultado);
        assertEquals(Status.INATIVO, ciclistaResponse.status());
        verify(externoServiceGateway, times(1)).validacaoCartaoDeCredito(any());
        verify(ciclistaRepository, times(1)).save(any(Ciclista.class));
        verify(cartaoService, times(1)).cadastrarCartaoDeCredito(any(), any(Ciclista.class));
        verify(externoServiceGateway, times(1)).confirmacaoCadastroEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar ciclista com email já cadastrado")
    void deveLancarExcecaoAoCriarCiclistaComEmailJaCadastrado() {
        when(ciclistaRepository.existsByEmail(anyString())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ciclistaService.criarCiclista(criarCiclistaRequest)
        );

        assertEquals("Email já cadastrado!", exception.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar ciclista brasileiro sem CPF")
    void deveLancarExcecaoAoCriarCiclistaBrasileiroSemCpf() {
        CriarCiclistaRequest requestSemCpf = new CriarCiclistaRequest(
                "João Silva",
                Nacionalidade.BRASILEIRO,
                LocalDate.of(1990, 1, 1),
                null,
                "joao@email.com",
                "senha123",
                "senha123",
                null,
                cartaoRequest,
                "http://foto.com/doc.jpg"
        );

        when(ciclistaRepository.existsByEmail(anyString())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ciclistaService.criarCiclista(requestSemCpf)
        );

        assertEquals("Ciclistas brasileiros devem fornecer CPF!", exception.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar ciclista estrangeiro sem passaporte")
    void deveLancarExcecaoAoCriarCiclistaEstrangeiroSemPassaporte() {
        CriarCiclistaRequest requestSemPassaporte = new CriarCiclistaRequest(
                "John Doe",
                Nacionalidade.ESTRANGEIRO,
                LocalDate.of(1990, 1, 1),
                "12345678901",
                "john@email.com",
                "senha123",
                "senha123",
                null,
                cartaoRequest,
                "http://foto.com/doc.jpg"
        );

        when(ciclistaRepository.existsByEmail(anyString())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ciclistaService.criarCiclista(requestSemPassaporte)
        );

        assertEquals("Ciclistas estrangeiros devem fornecer passaporte!", exception.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senhas não coincidem")
    void deveLancarExcecaoQuandoSenhasNaoCoincidem() {
        CriarCiclistaRequest requestSenhasDiferentes = new CriarCiclistaRequest(
                "João Silva",
                Nacionalidade.BRASILEIRO,
                LocalDate.of(1990, 1, 1),
                "12345678901",
                "joao@email.com",
                "senha123",
                "senha456",
                passaporteRequest,
                cartaoRequest,
                "http://foto.com/doc.jpg"

        );

        when(ciclistaRepository.existsByEmail(anyString())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ciclistaService.criarCiclista(requestSenhasDiferentes)
        );

        assertEquals("Senha e confirmação de senha não coincidem!", exception.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    @DisplayName("Deve atualizar ciclista com sucesso")
    void deveAtualizarCiclistaComSucesso() {
        when(ciclistaRepository.findById(1L)).thenReturn(Optional.of(ciclista));
        when(ciclistaRepository.save(any(Ciclista.class))).thenReturn(ciclista);
        when(ciclistaMapper.toCiclistaResponse(any(Ciclista.class))).thenReturn(ciclistaResponse);
        doNothing().when(ciclistaMapper).updateCiclistaFromRequest(any(), any(Ciclista.class));
        when(externoServiceGateway.atualizacaoCiclistaEmail(anyString(), anyString())).thenReturn(true);

        CiclistaResponse resultado = ciclistaService.atualizarCiclista(1L, atualizarCiclistaRequest);

        assertNotNull(resultado);
        verify(ciclistaRepository, times(1)).findById(1L);
        verify(ciclistaRepository, times(1)).save(any(Ciclista.class));
        verify(externoServiceGateway, times(1)).atualizacaoCiclistaEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar ciclista inexistente")
    void deveLancarExcecaoAoAtualizarCiclistaInexistente() {
        when(ciclistaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> ciclistaService.atualizarCiclista(1L, atualizarCiclistaRequest));
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    @DisplayName("Deve ativar ciclista com sucesso")
    void deveAtivarCiclistaComSucesso() {
        ciclista.setStatus(Status.INATIVO);
        when(ciclistaRepository.findById(1L)).thenReturn(Optional.of(ciclista));
        when(ciclistaRepository.save(any(Ciclista.class))).thenReturn(ciclista);
        when(ciclistaMapper.toCiclistaResponse(any(Ciclista.class))).thenReturn(ciclistaResponse);

        CiclistaResponse resultado = ciclistaService.ativarCiclista(1L);

        assertNotNull(resultado);
        verify(ciclistaRepository, times(1)).save(any(Ciclista.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar ativar ciclista já ativo")
    void deveLancarExcecaoAoTentarAtivarCiclistaJaAtivo() {
        ciclista.setStatus(Status.ATIVO);
        when(ciclistaRepository.findById(1L)).thenReturn(Optional.of(ciclista));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ciclistaService.ativarCiclista(1L)
        );

        assertEquals("Ciclista já está ativo!", exception.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    @DisplayName("Deve verificar existência de email corretamente")
    void deveVerificarExistenciaDeEmailCorretamente() {
        when(ciclistaRepository.existsByEmail("joao@email.com")).thenReturn(true);
        when(ciclistaRepository.existsByEmail("outro@email.com")).thenReturn(false);

        assertTrue(ciclistaService.verificarExistenciaEmail("joao@email.com"));
        assertFalse(ciclistaService.verificarExistenciaEmail("outro@email.com"));
        verify(ciclistaRepository, times(2)).existsByEmail(anyString());
    }
}

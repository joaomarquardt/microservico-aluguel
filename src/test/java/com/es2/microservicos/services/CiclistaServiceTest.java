package com.es2.microservicos.services;

import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Nacionalidade;
import com.es2.microservicos.domain.Passaporte;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.requests.AtualizarCiclistaRequest;
import com.es2.microservicos.dtos.requests.CriarCiclistaRequest;
import com.es2.microservicos.dtos.requests.RegistrarPassaporteRequest;
import com.es2.microservicos.dtos.responses.CartaoResponse;
import com.es2.microservicos.dtos.responses.CiclistaResponse;
import com.es2.microservicos.external.gateways.EquipamentoServiceGateway;
import com.es2.microservicos.external.gateways.ExternoServiceGateway;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private ExternoServiceGateway externoServiceGateway;

    @Mock
    private EquipamentoServiceGateway equipamentoServiceGateway;

    @InjectMocks
    private CiclistaService ciclistaService;

    private Ciclista ciclista;
    private CriarCiclistaRequest criarRequest;
    private AtualizarCiclistaRequest atualizarRequest;
    private CiclistaResponse ciclistaResponse;
    private AdicionarCartaoRequest cartaoRequest;

    @BeforeEach
    void setUp() {
        cartaoRequest = new AdicionarCartaoRequest(
                "João Silva",
                "1234567890123456",
                LocalDate.of(2027, 12, 31),
                "123"
        );

        ciclista = new Ciclista();
        ciclista.setId(1L);
        ciclista.setNome("João Silva");
        ciclista.setEmail("joao@email.com");
        ciclista.setStatus(Status.INATIVO);
        ciclista.setCpf("12345678901");
        ciclista.setNascimento(LocalDate.of(1990, 1, 1));
        ciclista.setNacionalidade(Nacionalidade.BRASILEIRO);

        criarRequest = new CriarCiclistaRequest(
                "João Silva",
                Nacionalidade.BRASILEIRO,
                LocalDate.of(1990, 1, 1),
                "12345678901",
                "joao@email.com",
                "senha123",
                "senha123",
                null,
                cartaoRequest,
                "http://foto.com/doc.jpg"
        );

        atualizarRequest = new AtualizarCiclistaRequest(
                "João Silva Atualizado",
                Nacionalidade.BRASILEIRO,
                LocalDate.of(1990, 1, 1),
                "12345678901",
                "joao.atualizado@email.com",
                "senha123",
                "senha123",
                null,
                "http://foto.com/doc.jpg"
        );

        ciclistaResponse = new CiclistaResponse(
                1L,
                Status.INATIVO,
                "João Silva",
                LocalDate.of(1990, 1, 1),
                "12345678901",
                null,
                Nacionalidade.BRASILEIRO,
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
        when(ciclistaRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ciclistaService.obterCiclistaPorId(1L)
        );

        assertEquals("Ciclista não encontrado!", exception.getMessage());
        verify(ciclistaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve criar ciclista brasileiro com sucesso")
    void deveCriarCiclistaBrasileiroComSucesso() {
        when(ciclistaRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(externoServiceGateway.validacaoCartaoDeCredito(any()))
                .thenReturn(ResponseEntity.ok().build());
        when(ciclistaMapper.toCiclista(any(CriarCiclistaRequest.class))).thenReturn(ciclista);
        when(ciclistaRepository.save(any(Ciclista.class))).thenReturn(ciclista);
        when(cartaoService.cadastrarCartaoDeCredito(any(), any(Ciclista.class)))
                .thenReturn(mock(CartaoResponse.class));
        when(externoServiceGateway.confirmacaoCadastroEmail(anyString(), anyString()))
                .thenReturn(ResponseEntity.ok().build());
        when(ciclistaMapper.toCiclistaResponse(any(Ciclista.class))).thenReturn(ciclistaResponse);

        CiclistaResponse resultado = ciclistaService.criarCiclista(criarRequest);

        assertNotNull(resultado);
        assertEquals(Status.INATIVO, resultado.status());
        verify(ciclistaRepository, times(1)).save(any(Ciclista.class));
        verify(cartaoService, times(1)).cadastrarCartaoDeCredito(any(), any(Ciclista.class));
        verify(externoServiceGateway, times(1)).confirmacaoCadastroEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar ciclista com email já cadastrado")
    void deveLancarExcecaoAoCriarCiclistaComEmailJaCadastrado() {
        when(ciclistaRepository.existsByEmail("joao@email.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ciclistaService.criarCiclista(criarRequest)
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
                null,
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
                null,
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
    @DisplayName("Deve lançar exceção quando cartão é inválido")
    void deveLancarExcecaoQuandoCartaoInvalido() {
        when(ciclistaRepository.existsByEmail(anyString())).thenReturn(false);
        when(externoServiceGateway.validacaoCartaoDeCredito(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ciclistaService.criarCiclista(criarRequest)
        );

        assertEquals("Cartão de crédito inválido!", exception.getMessage());
        verify(ciclistaRepository, never()).save(any(Ciclista.class));
    }

    @Test
    @DisplayName("Deve atualizar ciclista com sucesso")
    void deveAtualizarCiclistaComSucesso() {
        when(ciclistaRepository.findById(1L)).thenReturn(Optional.of(ciclista));
        doNothing().when(ciclistaMapper).updateCiclistaFromRequest(any(), any(Ciclista.class));
        when(ciclistaRepository.save(any(Ciclista.class))).thenReturn(ciclista);
        when(externoServiceGateway.atualizacaoCiclistaEmail(anyString(), anyString()))
                .thenReturn(ResponseEntity.ok().build());
        when(ciclistaMapper.toCiclistaResponse(any(Ciclista.class))).thenReturn(ciclistaResponse);

        CiclistaResponse resultado = ciclistaService.atualizarCiclista(1L, atualizarRequest);

        assertNotNull(resultado);
        verify(ciclistaRepository, times(1)).save(any(Ciclista.class));
        verify(externoServiceGateway, times(1)).atualizacaoCiclistaEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando falha envio de email ao atualizar")
    void deveLancarExcecaoQuandoFalhaEnvioEmailAoAtualizar() {
        when(ciclistaRepository.findById(1L)).thenReturn(Optional.of(ciclista));
        doNothing().when(ciclistaMapper).updateCiclistaFromRequest(any(), any(Ciclista.class));
        when(ciclistaRepository.save(any(Ciclista.class))).thenReturn(ciclista);
        when(externoServiceGateway.atualizacaoCiclistaEmail(anyString(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ciclistaService.atualizarCiclista(1L, atualizarRequest)
        );

        assertEquals("Erro ao enviar email de atualização de ciclista!", exception.getMessage());
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
    }

    @Test
    @DisplayName("Deve retornar null ao obter bicicleta alugada (TODO)")
    void deveRetornarNullAoObterBicicletaAlugada() {
        var resultado = ciclistaService.obterBicicletaAlugadaPorIdCiclista(1L);
        assertNull(resultado);
        verify(equipamentoServiceGateway, times(1)).obterBicicletaPorId(1L);
    }

    @Test
    @DisplayName("Deve criar ciclista estrangeiro com passaporte válido")
    void deveCriarCiclistaEstrangeiroComPassaporteValido() {
        Passaporte passaporte = new Passaporte("ABC123456", LocalDate.of(2030, 12, 31), "Estados Unidos");
        CriarCiclistaRequest requestEstrangeiro = getCriarCiclistaRequest();

        Ciclista ciclistaEstrangeiro = new Ciclista();
        ciclistaEstrangeiro.setId(2L);
        ciclistaEstrangeiro.setNome("John Doe");
        ciclistaEstrangeiro.setEmail("john@email.com");
        ciclistaEstrangeiro.setStatus(Status.INATIVO);
        ciclistaEstrangeiro.setNacionalidade(Nacionalidade.ESTRANGEIRO);
        ciclistaEstrangeiro.setPassaporte(passaporte);

        when(ciclistaRepository.existsByEmail(anyString())).thenReturn(false);
        when(externoServiceGateway.validacaoCartaoDeCredito(any()))
                .thenReturn(ResponseEntity.ok().build());
        when(ciclistaMapper.toCiclista(any(CriarCiclistaRequest.class))).thenReturn(ciclistaEstrangeiro);
        when(ciclistaRepository.save(any(Ciclista.class))).thenReturn(ciclistaEstrangeiro);
        when(cartaoService.cadastrarCartaoDeCredito(any(), any(Ciclista.class)))
                .thenReturn(mock(CartaoResponse.class));
        when(externoServiceGateway.confirmacaoCadastroEmail(anyString(), anyString()))
                .thenReturn(ResponseEntity.ok().build());
        when(ciclistaMapper.toCiclistaResponse(any(Ciclista.class))).thenReturn(ciclistaResponse);

        CiclistaResponse resultado = ciclistaService.criarCiclista(requestEstrangeiro);

        assertNotNull(resultado);
        verify(ciclistaRepository, times(1)).save(any(Ciclista.class));
    }

    private CriarCiclistaRequest getCriarCiclistaRequest() {
        RegistrarPassaporteRequest registrarPassaporteRequest = new RegistrarPassaporteRequest("ABC123456",LocalDate.of(2030, 12, 31), "Estados Unidos");
        return new CriarCiclistaRequest(
                "John Doe",
                Nacionalidade.ESTRANGEIRO,
                LocalDate.of(1990, 1, 1),
                null,
                "john@email.com",
                "senha123",
                "senha123",
                registrarPassaporteRequest,
                cartaoRequest,
                "http://foto.com/doc.jpg"
        );
    }
}

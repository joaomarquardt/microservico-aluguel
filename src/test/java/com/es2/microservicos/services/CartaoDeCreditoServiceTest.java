package com.es2.microservicos.services;

import com.es2.microservicos.domain.CartaoDeCredito;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Nacionalidade;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.responses.CartaoResponse;
import com.es2.microservicos.external.gateways.ExternoServiceGateway;
import com.es2.microservicos.mappers.CartaoDeCreditoMapper;
import com.es2.microservicos.repositories.CartaoDeCreditoRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CartaoDeCreditoService")
class CartaoDeCreditoServiceTest {

    @Mock
    private CartaoDeCreditoRepository cartaoDeCreditoRepository;

    @Mock
    private CartaoDeCreditoMapper cartaoMapper;

    @Mock
    private ExternoServiceGateway externoServiceGateway;

    @InjectMocks
    private CartaoDeCreditoService cartaoDeCreditoService;

    private Ciclista ciclista;
    private CartaoDeCredito cartaoDeCredito;
    private AdicionarCartaoRequest cartaoRequest;
    private CartaoResponse cartaoResponse;

    @BeforeEach
    void setUp() {
        ciclista = new Ciclista();
        ciclista.setId(1L);
        ciclista.setNome("João Silva");
        ciclista.setEmail("joao@example.com");
        ciclista.setCpf("12345678900");
        ciclista.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclista.setUrlFotoDocumento("http://foto.com");
        ciclista.setStatus(Status.ATIVO);

        cartaoRequest = new AdicionarCartaoRequest(
                "João Silva",
                "1234567890123456",
                LocalDate.of(2030, 12, 31),
                "123"
        );

        cartaoDeCredito = new CartaoDeCredito();
        cartaoDeCredito.setId(1L);
        cartaoDeCredito.setNomeTitular("João Silva");
        cartaoDeCredito.setNumero("1234567890123456");
        cartaoDeCredito.setValidade(LocalDate.of(2025, 12, 31));
        cartaoDeCredito.setCvv("123");
        cartaoDeCredito.setCiclista(ciclista);

        cartaoResponse = new CartaoResponse(
                1L,
                "João Silva",
                "1234567890123456",
                LocalDate.of(2025, 12, 31),
                "123"
        );
    }

    @Test
    @DisplayName("Deve cadastrar cartão de crédito com sucesso")
    void deveCadastrarCartaoDeCreditoComSucesso() {
        when(cartaoMapper.toCartaoDeCredito(cartaoRequest)).thenReturn(cartaoDeCredito);
        when(cartaoDeCreditoRepository.save(any(CartaoDeCredito.class))).thenReturn(cartaoDeCredito);
        when(cartaoMapper.toCartaoResponse(cartaoDeCredito)).thenReturn(cartaoResponse);
        CartaoResponse resultado = cartaoDeCreditoService.cadastrarCartaoDeCredito(cartaoRequest, ciclista);
        assertThat(resultado).isNotNull();
        assertThat(resultado.nomeTitular()).isEqualTo("João Silva");
        assertThat(resultado.numero()).isEqualTo("1234567890123456");
        assertThat(resultado.cvv()).isEqualTo("123");
        verify(cartaoMapper).toCartaoDeCredito(cartaoRequest);
        verify(cartaoDeCreditoRepository).save(any(CartaoDeCredito.class));
        verify(cartaoMapper).toCartaoResponse(cartaoDeCredito);
    }

    @Test
    @DisplayName("Deve obter entidade de cartão de crédito por ID do ciclista")
    void deveObterEntidadeCartaoDeCreditoPorIdCiclista() {
        when(cartaoDeCreditoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));
        CartaoDeCredito resultado = cartaoDeCreditoService.obterEntidadeCartaoDeCreditoPorIdCiclista(1L);
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNomeTitular()).isEqualTo("João Silva");
        assertThat(resultado.getNumero()).isEqualTo("1234567890123456");
        verify(cartaoDeCreditoRepository).findByCiclistaId(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando cartão não encontrado por ID do ciclista")
    void deveLancarExcecaoQuandoCartaoNaoEncontradoPorIdCiclista() {
        when(cartaoDeCreditoRepository.findByCiclistaId(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cartaoDeCreditoService.obterEntidadeCartaoDeCreditoPorIdCiclista(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Cartão de crédito não encontrado para ciclista com ID: 1");
        verify(cartaoDeCreditoRepository).findByCiclistaId(1L);
    }

    @Test
    @DisplayName("Deve obter cartão de crédito response por ID do ciclista")
    void deveObterCartaoDeCreditoResponsePorIdCiclista() {
        when(cartaoDeCreditoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));
        when(cartaoMapper.toCartaoResponse(cartaoDeCredito)).thenReturn(cartaoResponse);
        CartaoResponse resultado = cartaoDeCreditoService.obterCartaoDeCreditoPorIdCiclista(1L);
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nomeTitular()).isEqualTo("João Silva");
        verify(cartaoDeCreditoRepository).findByCiclistaId(1L);
        verify(cartaoMapper).toCartaoResponse(cartaoDeCredito);
    }

    @Test
    @DisplayName("Deve atualizar cartão de crédito por ID do ciclista com sucesso")
    void deveAtualizarCartaoDeCreditoPorIdCiclistaComSucesso() {
        AdicionarCartaoRequest novoCartaoRequest = new AdicionarCartaoRequest(
                "João Silva Atualizado",
                "9876543210987654",
                LocalDate.of(2025, 12, 31),
                "456"
        );
        when(cartaoDeCreditoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));
        doNothing().when(externoServiceGateway).validacaoCartaoDeCredito(novoCartaoRequest);
        doNothing().when(cartaoMapper).updateCartaoDeCreditoFromRequest(novoCartaoRequest, cartaoDeCredito);
        when(cartaoDeCreditoRepository.save(cartaoDeCredito)).thenReturn(cartaoDeCredito);
        doNothing().when(externoServiceGateway).atualizacaoCartaoEmail(anyString());
        cartaoDeCreditoService.atualizarCartaoDeCreditoPorIdCiclista(1L, novoCartaoRequest);
        verify(cartaoDeCreditoRepository).findByCiclistaId(1L);
        verify(externoServiceGateway).validacaoCartaoDeCredito(novoCartaoRequest);
        verify(cartaoMapper).updateCartaoDeCreditoFromRequest(novoCartaoRequest, cartaoDeCredito);
        verify(cartaoDeCreditoRepository).save(cartaoDeCredito);
        verify(externoServiceGateway).atualizacaoCartaoEmail("joao@example.com");
    }

    @Test
    @DisplayName("Deve validar cartão antes de atualizar")
    void deveValidarCartaoAntesDeAtualizar() {
        AdicionarCartaoRequest novoCartaoRequest = new AdicionarCartaoRequest(
                "João Silva",
                "9876543210987654",
                LocalDate.of(2025, 12, 31),
                "456"
        );
        when(cartaoDeCreditoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));
        doNothing().when(externoServiceGateway).validacaoCartaoDeCredito(novoCartaoRequest);
        doNothing().when(cartaoMapper).updateCartaoDeCreditoFromRequest(novoCartaoRequest, cartaoDeCredito);
        when(cartaoDeCreditoRepository.save(cartaoDeCredito)).thenReturn(cartaoDeCredito);
        doNothing().when(externoServiceGateway).atualizacaoCartaoEmail(anyString());
        cartaoDeCreditoService.atualizarCartaoDeCreditoPorIdCiclista(1L, novoCartaoRequest);
        verify(externoServiceGateway).validacaoCartaoDeCredito(novoCartaoRequest);
    }

    @Test
    @DisplayName("Deve enviar email após atualizar cartão")
    void deveEnviarEmailAposAtualizarCartao() {
        AdicionarCartaoRequest novoCartaoRequest = new AdicionarCartaoRequest(
                "João Silva",
                "9876543210987654",
                LocalDate.of(2025, 12, 31),
                "456"
        );
        when(cartaoDeCreditoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));
        doNothing().when(externoServiceGateway).validacaoCartaoDeCredito(novoCartaoRequest);
        doNothing().when(cartaoMapper).updateCartaoDeCreditoFromRequest(novoCartaoRequest, cartaoDeCredito);
        when(cartaoDeCreditoRepository.save(cartaoDeCredito)).thenReturn(cartaoDeCredito);
        doNothing().when(externoServiceGateway).atualizacaoCartaoEmail(anyString());
        cartaoDeCreditoService.atualizarCartaoDeCreditoPorIdCiclista(1L, novoCartaoRequest);
        verify(externoServiceGateway).atualizacaoCartaoEmail("joao@example.com");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar cartão inexistente")
    void deveLancarExcecaoAoTentarAtualizarCartaoInexistente() {
        AdicionarCartaoRequest novoCartaoRequest = new AdicionarCartaoRequest(
                "João Silva",
                "9876543210987654",
                LocalDate.of(2025, 12, 31),
                "456"
        );
        when(cartaoDeCreditoRepository.findByCiclistaId(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cartaoDeCreditoService.atualizarCartaoDeCreditoPorIdCiclista(1L, novoCartaoRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Cartão de crédito não encontrado para ciclista com ID: 1");
        verify(cartaoDeCreditoRepository).findByCiclistaId(1L);
        verify(externoServiceGateway, never()).validacaoCartaoDeCredito(any());
        verify(cartaoDeCreditoRepository, never()).save(any());
        verify(externoServiceGateway, never()).atualizacaoCartaoEmail(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação do cartão falha na atualização")
    void deveLancarExcecaoQuandoValidacaoFalhaNaAtualizacao() {
        AdicionarCartaoRequest cartaoInvalido = new AdicionarCartaoRequest(
                "João Silva",
                "0000000000000000",
                LocalDate.of(2025, 12, 31),
                "000"
        );
        when(cartaoDeCreditoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));
        doThrow(new IllegalArgumentException("Validação do cartão de crédito falhou!"))
                .when(externoServiceGateway).validacaoCartaoDeCredito(cartaoInvalido);
        assertThatThrownBy(() -> cartaoDeCreditoService.atualizarCartaoDeCreditoPorIdCiclista(1L, cartaoInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Validação do cartão de crédito falhou!");
        verify(externoServiceGateway).validacaoCartaoDeCredito(cartaoInvalido);
        verify(cartaoDeCreditoRepository, never()).save(any());
        verify(externoServiceGateway, never()).atualizacaoCartaoEmail(anyString());
    }

    @Test
    @DisplayName("Deve persistir alterações do cartão no banco de dados")
    void devePersistirAlteracoesCartaoNoBancoDeDados() {
        AdicionarCartaoRequest novoCartaoRequest = new AdicionarCartaoRequest(
                "João Silva Atualizado",
                "9876543210987654",
                LocalDate.of(2025, 12, 31),
                "456"
        );
        when(cartaoDeCreditoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));
        doNothing().when(externoServiceGateway).validacaoCartaoDeCredito(novoCartaoRequest);
        doNothing().when(cartaoMapper).updateCartaoDeCreditoFromRequest(novoCartaoRequest, cartaoDeCredito);
        when(cartaoDeCreditoRepository.save(cartaoDeCredito)).thenReturn(cartaoDeCredito);
        doNothing().when(externoServiceGateway).atualizacaoCartaoEmail(anyString());
        cartaoDeCreditoService.atualizarCartaoDeCreditoPorIdCiclista(1L, novoCartaoRequest);
        verify(cartaoDeCreditoRepository).save(cartaoDeCredito);
    }

    @Test
    @DisplayName("Deve retornar cartão com dados corretos após cadastro")
    void deveRetornarCartaoComDadosCorretosAposCadastro() {
        when(cartaoMapper.toCartaoDeCredito(cartaoRequest)).thenReturn(cartaoDeCredito);
        when(cartaoDeCreditoRepository.save(any(CartaoDeCredito.class))).thenReturn(cartaoDeCredito);
        when(cartaoMapper.toCartaoResponse(cartaoDeCredito)).thenReturn(cartaoResponse);
        CartaoResponse resultado = cartaoDeCreditoService.cadastrarCartaoDeCredito(cartaoRequest, ciclista);
        assertThat(resultado).isNotNull();
        assertThat(resultado.nomeTitular()).isEqualTo("João Silva");
        assertThat(resultado.numero()).isEqualTo("1234567890123456");
        assertThat(resultado.validade()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(resultado.cvv()).isEqualTo("123");
    }
}

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CartaoDeCreditoService")
class CartaoDeCreditoServiceTest {

    @Mock
    private CartaoDeCreditoRepository cartaoRepository;

    @Mock
    private CartaoDeCreditoMapper cartaoMapper;

    @Mock
    private ExternoServiceGateway externoServiceGateway;

    @InjectMocks
    private CartaoDeCreditoService cartaoService;

    private CartaoDeCredito cartaoDeCredito;
    private AdicionarCartaoRequest cartaoRequest;
    private CartaoResponse cartaoResponse;
    private Ciclista ciclista;

    @BeforeEach
    void setUp() {
        ciclista = new Ciclista();
        ciclista.setId(1L);
        ciclista.setNome("João Silva");
        ciclista.setEmail("joao@email.com");
        ciclista.setStatus(Status.ATIVO);
        ciclista.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclista.setCpf("12345678901");

        cartaoDeCredito = new CartaoDeCredito();
        cartaoDeCredito.setId(1L);
        cartaoDeCredito.setNomeTitular("João Silva");
        cartaoDeCredito.setNumeroCartao("1234567890123456");
        cartaoDeCredito.setDataValidade(LocalDate.of(2027, 12, 31));
        cartaoDeCredito.setCodigoSeguranca("123");
        cartaoDeCredito.setCiclista(ciclista);

        cartaoRequest = new AdicionarCartaoRequest(
                "João Silva",
                "1234567890123456",
                LocalDate.of(2027, 12, 31),
                "123"
        );

        cartaoResponse = new CartaoResponse(
                1L,
                "João Silva",
                "1234567890123456",
                LocalDate.of(2027, 12, 31),
                "123"
        );
    }

    @Test
    @DisplayName("Deve cadastrar cartão de crédito com sucesso")
    void deveCadastrarCartaoDeCreditoComSucesso() {
        when(cartaoMapper.toCartaoDeCredito(any(AdicionarCartaoRequest.class))).thenReturn(cartaoDeCredito);
        when(cartaoRepository.save(any(CartaoDeCredito.class))).thenReturn(cartaoDeCredito);
        when(cartaoMapper.toCartaoResponse(any(CartaoDeCredito.class))).thenReturn(cartaoResponse);

        CartaoResponse resultado = cartaoService.cadastrarCartaoDeCredito(cartaoRequest, ciclista);

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.nomeTitular());
        assertEquals("1234567890123456", resultado.numeroCartao());
        verify(cartaoMapper, times(1)).toCartaoDeCredito(cartaoRequest);
        verify(cartaoRepository, times(1)).save(any(CartaoDeCredito.class));
        verify(cartaoMapper, times(1)).toCartaoResponse(cartaoDeCredito);
    }

    @Test
    @DisplayName("Deve associar ciclista ao cartão ao cadastrar")
    void deveAssociarCiclistaAoCartaoAoCadastrar() {
        when(cartaoMapper.toCartaoDeCredito(any(AdicionarCartaoRequest.class))).thenReturn(cartaoDeCredito);
        when(cartaoRepository.save(any(CartaoDeCredito.class))).thenAnswer(invocation -> {
            CartaoDeCredito cartao = invocation.getArgument(0);
            assertEquals(ciclista, cartao.getCiclista());
            return cartao;
        });
        when(cartaoMapper.toCartaoResponse(any(CartaoDeCredito.class))).thenReturn(cartaoResponse);

        cartaoService.cadastrarCartaoDeCredito(cartaoRequest, ciclista);

        verify(cartaoRepository, times(1)).save(argThat(cartao ->
                cartao.getCiclista() != null && cartao.getCiclista().equals(ciclista)
        ));
    }

    @Test
    @DisplayName("Deve obter entidade de cartão de crédito por ID do ciclista")
    void deveObterEntidadeCartaoDeCreditoPorIdDoCiclista() {
        when(cartaoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));

        CartaoDeCredito resultado = cartaoService.obterEntidadeCartaoDeCreditoPorIdCiclista(1L);

        assertNotNull(resultado);
        assertEquals(cartaoDeCredito.getId(), resultado.getId());
        assertEquals(cartaoDeCredito.getNomeTitular(), resultado.getNomeTitular());
        verify(cartaoRepository, times(1)).findByCiclistaId(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando cartão não encontrado por ID do ciclista")
    void deveLancarExcecaoQuandoCartaoNaoEncontradoPorIdCiclista() {
        when(cartaoRepository.findByCiclistaId(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartaoService.obterEntidadeCartaoDeCreditoPorIdCiclista(1L)
        );

        assertEquals("Cartão de crédito não encontrado para ciclista com ID: 1", exception.getMessage());
        verify(cartaoRepository, times(1)).findByCiclistaId(1L);
    }

    @Test
    @DisplayName("Deve obter cartão de crédito response por ID do ciclista")
    void deveObterCartaoDeCreditoResponsePorIdDoCiclista() {
        when(cartaoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));
        when(cartaoMapper.toCartaoResponse(any(CartaoDeCredito.class))).thenReturn(cartaoResponse);

        CartaoResponse resultado = cartaoService.obterCartaoDeCreditoPorIdCiclista(1L);

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.nomeTitular());
        assertEquals("1234567890123456", resultado.numeroCartao());
        verify(cartaoRepository, times(1)).findByCiclistaId(1L);
        verify(cartaoMapper, times(1)).toCartaoResponse(cartaoDeCredito);
    }

    @Test
    @DisplayName("Deve atualizar cartão de crédito com sucesso")
    void deveAtualizarCartaoDeCreditoComSucesso() {
        AdicionarCartaoRequest novoCartao = new AdicionarCartaoRequest(
                "João Silva",
                "9876543210987654",
                LocalDate.of(2029, 6, 30),
                "456"
        );

        when(cartaoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));
        when(externoServiceGateway.validacaoCartaoDeCredito(any(AdicionarCartaoRequest.class)))
                .thenReturn(ResponseEntity.ok().build());
        doNothing().when(cartaoMapper).updateCartaoDeCreditoFromRequest(any(), any(CartaoDeCredito.class));
        when(cartaoRepository.save(any(CartaoDeCredito.class))).thenReturn(cartaoDeCredito);
        when(externoServiceGateway.atualizacaoCartaoEmail(anyString(), anyString()))
                .thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() -> cartaoService.atualizarCartaoDeCreditoPorIdCiclista(1L, novoCartao));

        verify(cartaoRepository, times(1)).findByCiclistaId(1L);
        verify(externoServiceGateway, times(1)).validacaoCartaoDeCredito(novoCartao);
        verify(cartaoMapper, times(1)).updateCartaoDeCreditoFromRequest(novoCartao, cartaoDeCredito);
        verify(cartaoRepository, times(1)).save(cartaoDeCredito);
        verify(externoServiceGateway, times(1)).atualizacaoCartaoEmail(ciclista.getNome(), ciclista.getEmail());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com cartão inválido")
    void deveLancarExcecaoAoAtualizarComCartaoInvalido() {
        when(cartaoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));
        when(externoServiceGateway.validacaoCartaoDeCredito(any(AdicionarCartaoRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartaoService.atualizarCartaoDeCreditoPorIdCiclista(1L, cartaoRequest)
        );

        assertEquals("Cartão de crédito inválido!", exception.getMessage());
        verify(cartaoRepository, times(1)).findByCiclistaId(1L);
        verify(cartaoRepository, never()).save(any(CartaoDeCredito.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cartão inexistente")
    void deveLancarExcecaoAoAtualizarCartaoInexistente() {
        when(cartaoRepository.findByCiclistaId(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartaoService.atualizarCartaoDeCreditoPorIdCiclista(1L, cartaoRequest)
        );

        assertEquals("Cartão de crédito não encontrado para ciclista com ID: 1", exception.getMessage());
        verify(cartaoRepository, times(1)).findByCiclistaId(1L);
        verify(externoServiceGateway, never()).validacaoCartaoDeCredito(any());
        verify(cartaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando falha envio de email ao atualizar cartão")
    void deveLancarExcecaoQuandoFalhaEnvioEmailAoAtualizarCartao() {
        when(cartaoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoDeCredito));
        when(externoServiceGateway.validacaoCartaoDeCredito(any()))
                .thenReturn(ResponseEntity.ok().build());
        doNothing().when(cartaoMapper).updateCartaoDeCreditoFromRequest(any(), any());
        when(cartaoRepository.save(any())).thenReturn(cartaoDeCredito);
        when(externoServiceGateway.atualizacaoCartaoEmail(anyString(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartaoService.atualizarCartaoDeCreditoPorIdCiclista(1L, cartaoRequest)
        );

        assertEquals("Erro ao enviar email de atualização do cartão de crédito!", exception.getMessage());
        verify(externoServiceGateway, times(1)).atualizacaoCartaoEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve processar múltiplos cadastros de cartão")
    void deveProcessarMultiplosCadastrosDeCartao() {
        Ciclista ciclista2 = new Ciclista();
        ciclista2.setId(2L);
        ciclista2.setNome("Maria Santos");
        ciclista2.setEmail("maria@email.com");

        when(cartaoMapper.toCartaoDeCredito(any())).thenReturn(cartaoDeCredito);
        when(cartaoRepository.save(any())).thenReturn(cartaoDeCredito);
        when(cartaoMapper.toCartaoResponse(any())).thenReturn(cartaoResponse);

        CartaoResponse resultado1 = cartaoService.cadastrarCartaoDeCredito(cartaoRequest, ciclista);
        CartaoResponse resultado2 = cartaoService.cadastrarCartaoDeCredito(cartaoRequest, ciclista2);

        assertNotNull(resultado1);
        assertNotNull(resultado2);
        verify(cartaoRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Deve obter cartão com data de validade futura")
    void deveObterCartaoComDataValidadeFutura() {
        CartaoDeCredito cartaoFuturo = new CartaoDeCredito();
        cartaoFuturo.setId(2L);
        cartaoFuturo.setDataValidade(LocalDate.of(2030, 12, 31));
        cartaoFuturo.setCiclista(ciclista);

        CartaoResponse responseFuturo = new CartaoResponse(
                1L, "Titular", "1111222233334444", LocalDate.of(2030, 12, 31), "999"
        );

        when(cartaoRepository.findByCiclistaId(1L)).thenReturn(Optional.of(cartaoFuturo));
        when(cartaoMapper.toCartaoResponse(cartaoFuturo)).thenReturn(responseFuturo);

        CartaoResponse resultado = cartaoService.obterCartaoDeCreditoPorIdCiclista(1L);

        assertTrue(resultado.dataValidade().isAfter(LocalDate.now()));
        verify(cartaoRepository, times(1)).findByCiclistaId(1L);
    }
}

package com.es2.microservicos.external.gateways;

import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.dtos.responses.CartaoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ExternoServiceGateway")
class ExternoServiceGatewayTest {

    @Mock
    private RestClient restClient;

    private ExternoServiceGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = new ExternoServiceGateway(restClient);
    }

    @Test
    @DisplayName("Deve retornar OK ao confirmar cadastro por email")
    void deveRetornarOkAoConfirmarCadastroPorEmail() {
        ResponseEntity response = gateway.confirmacaoCadastroEmail("João Silva", "joao@email.com");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar OK ao enviar email de atualização de ciclista")
    void deveRetornarOkAoEnviarEmailAtualizacaoCiclista() {
        ResponseEntity response = gateway.atualizacaoCiclistaEmail("Maria Santos", "maria@email.com");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar OK ao enviar email de atualização de cartão")
    void deveRetornarOkAoEnviarEmailAtualizacaoCartao() {
        ResponseEntity response = gateway.atualizacaoCartaoEmail("Carlos Silva", "carlos@email.com");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar OK ao enviar dados de aluguel atual")
    void deveRetornarOkAoEnviarDadosAluguelAtual() {
        AluguelResponse aluguel = new AluguelResponse(
                1L, 1L, LocalDateTime.now(), 1L, null, null, 10.0, 100L
        );

        ResponseEntity response = gateway.dadosAluguelAtualEmail(aluguel);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar OK ao enviar dados de novo aluguel")
    void deveRetornarOkAoEnviarDadosNovoAluguel() {
        AluguelResponse aluguel = new AluguelResponse(
                1L, 2L, LocalDateTime.now(), 2L, null, null, 10.0, 200L
        );

        ResponseEntity response = gateway.dadosAluguelNovoEmail(aluguel);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar OK ao cobrar aluguel")
    void deveRetornarOkAoCobrarAluguel() {
        CartaoResponse cartao = new CartaoResponse(
                1L, "João Silva", "1234567890123456", LocalDate.of(2027, 12, 31), "123"
        );

        ResponseEntity response = gateway.cobrarAluguel(10.0, cartao);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar OK ao validar cartão de crédito")
    void deveRetornarOkAoValidarCartaoDeCredito() {
        AdicionarCartaoRequest cartaoRequest = new AdicionarCartaoRequest(
                "Maria Santos", "9876543210987654", LocalDate.of(2028, 6, 30), "456"
        );

        ResponseEntity response = gateway.validacaoCartaoDeCredito(cartaoRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve processar múltiplas confirmações de email")
    void deveProcessarMultiplasConfirmacoesEmail() {
        ResponseEntity response1 = gateway.confirmacaoCadastroEmail("User1", "user1@email.com");
        ResponseEntity response2 = gateway.confirmacaoCadastroEmail("User2", "user2@email.com");
        ResponseEntity response3 = gateway.confirmacaoCadastroEmail("User3", "user3@email.com");

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    @DisplayName("Deve processar cobrança com diferentes valores")
    void deveProcessarCobrancaComDiferentesValores() {
        CartaoResponse cartao = new CartaoResponse(
                1L, "Titular", "1111222233334444", LocalDate.of(2029, 3, 15), "789"
        );

        ResponseEntity response1 = gateway.cobrarAluguel(5.0, cartao);
        ResponseEntity response2 = gateway.cobrarAluguel(15.0, cartao);
        ResponseEntity response3 = gateway.cobrarAluguel(25.50, cartao);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    @DisplayName("Deve aceitar valores nulos em notificações")
    void deveAceitarValoresNulosEmNotificacoes() {
        assertDoesNotThrow(() -> {
            gateway.confirmacaoCadastroEmail(null, null);
            gateway.atualizacaoCiclistaEmail(null, null);
            gateway.atualizacaoCartaoEmail(null, null);
        });
    }

    @Test
    @DisplayName("Deve aceitar aluguel nulo em notificações")
    void deveAceitarAluguelNuloEmNotificacoes() {
        assertDoesNotThrow(() -> {
            gateway.dadosAluguelAtualEmail(null);
            gateway.dadosAluguelNovoEmail(null);
        });
    }
}

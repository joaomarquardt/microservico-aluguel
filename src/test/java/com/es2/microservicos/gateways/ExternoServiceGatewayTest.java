package com.es2.microservicos.gateways;

import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ExternoServiceGateway")
class ExternoServiceGatewayTest {

    @Mock
    private RestClient restClient;

    @InjectMocks
    private ExternoServiceGateway externoServiceGateway;

    private AdicionarCartaoRequest cartaoRequest;

    @BeforeEach
    void setUp() {
        cartaoRequest = new AdicionarCartaoRequest(
                "João Silva",
                "1234567890123456",
                LocalDate.of(2027, 12, 31),
                "123"
        );
    }

    @Test
    @DisplayName("Deve confirmar cadastro por email e retornar true")
    void deveConfirmarCadastroPorEmail() {
        boolean resultado = externoServiceGateway.confirmacaoCadastroEmail("João Silva", "joao@email.com");

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve enviar email de atualização de ciclista e retornar true")
    void deveEnviarEmailAtualizacaoCiclista() {
        boolean resultado = externoServiceGateway.atualizacaoCiclistaEmail("João Silva", "joao@email.com");

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve enviar email de atualização de cartão e retornar true")
    void deveEnviarEmailAtualizacaoCartao() {
        boolean resultado = externoServiceGateway.atualizacaoCartaoEmail("João Silva", "joao@email.com");

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar cartão de crédito e retornar true")
    void deveValidarCartaoDeCredito() {
        boolean resultado = externoServiceGateway.validacaoCartaoDeCredito(cartaoRequest);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve criar NotificacaoRequest corretamente ao confirmar cadastro")
    void deveCriarNotificacaoRequestCorretamente() {
        String nome = "Maria Silva";
        String email = "maria@email.com";

        boolean resultado = externoServiceGateway.confirmacaoCadastroEmail(nome, email);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve processar validação com cartão válido")
    void deveProcessarValidacaoComCartaoValido() {
        AdicionarCartaoRequest cartaoValido = new AdicionarCartaoRequest(
                "Carlos Santos",
                "9876543210987654",
                LocalDate.of(2028, 6, 30),
                "456"
        );

        boolean resultado = externoServiceGateway.validacaoCartaoDeCredito(cartaoValido);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve enviar notificação de atualização mesmo com nome vazio")
    void deveEnviarNotificacaoComNomeVazio() {
        boolean resultado = externoServiceGateway.atualizacaoCiclistaEmail("", "email@test.com");

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve processar múltiplas confirmações de cadastro")
    void deveProcessarMultiplasConfirmacoes() {
        boolean resultado1 = externoServiceGateway.confirmacaoCadastroEmail("User1", "user1@email.com");
        boolean resultado2 = externoServiceGateway.confirmacaoCadastroEmail("User2", "user2@email.com");
        boolean resultado3 = externoServiceGateway.confirmacaoCadastroEmail("User3", "user3@email.com");

        assertTrue(resultado1);
        assertTrue(resultado2);
        assertTrue(resultado3);
    }

    @Test
    @DisplayName("Deve processar validação com diferentes cartões")
    void deveProcessarValidacaoComDiferentesCartoes() {
        AdicionarCartaoRequest cartao1 = new AdicionarCartaoRequest(
                "Titular 1", "1111222233334444", LocalDate.of(2025, 12, 31), "111"
        );
        AdicionarCartaoRequest cartao2 = new AdicionarCartaoRequest(
                "Titular 2", "5555666677778888", LocalDate.of(2026, 3, 31), "222"
        );

        boolean resultado1 = externoServiceGateway.validacaoCartaoDeCredito(cartao1);
        boolean resultado2 = externoServiceGateway.validacaoCartaoDeCredito(cartao2);

        assertTrue(resultado1);
        assertTrue(resultado2);
    }

    @Test
    @DisplayName("Deve enviar email de atualização de cartão para diferentes destinatários")
    void deveEnviarEmailAtualizacaoParaDiferentesDestinatarios() {
        boolean resultado1 = externoServiceGateway.atualizacaoCartaoEmail("João", "joao@email.com");
        boolean resultado2 = externoServiceGateway.atualizacaoCartaoEmail("Maria", "maria@email.com");

        assertTrue(resultado1);
        assertTrue(resultado2);
    }
}

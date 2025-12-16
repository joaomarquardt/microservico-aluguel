package com.es2.microservicos.external.gateways;

import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.requests.CobrancaRequest;
import com.es2.microservicos.dtos.requests.EmailRequest;
import com.es2.microservicos.dtos.requests.ValidacaoCartaoRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.dtos.responses.CobrancaResponse;
import com.es2.microservicos.dtos.responses.EmailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ExternoServiceGateway")
class ExternoServiceGatewayTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private ExternoServiceGateway gateway;

    private EmailResponse emailResponse;
    private CobrancaResponse cobrancaResponse;
    private AluguelResponse aluguelResponse;

    @BeforeEach
    void setUp() {
        gateway = new ExternoServiceGateway(restClient);

        emailResponse = new EmailResponse(1L, "email@gmail.com", "Email enviado com sucesso", "Corpo do email");
        cobrancaResponse = new CobrancaResponse(1L, "Cobrança processada", Instant.now().toString(), Instant.now().plusSeconds(3600).toString(), 10.0, 1L);

        aluguelResponse = new AluguelResponse(
                1L, 1L, LocalDateTime.now(), 1L, null, null,10.0, 1L
        );
    }

    @Test
    @DisplayName("Deve enviar email de confirmação de cadastro com sucesso")
    void deveEnviarEmailConfirmacaoCadastroComSucesso() {
        ResponseEntity<EmailResponse> responseEntity = ResponseEntity.ok(emailResponse);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(EmailRequest.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(EmailResponse.class)).thenReturn(responseEntity);
        gateway.confirmacaoCadastroEmail("teste@example.com");
        ArgumentCaptor<EmailRequest> captor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(requestBodyUriSpec).body(captor.capture());
        EmailRequest emailCapturado = captor.getValue();
        assertThat(emailCapturado.email()).isEqualTo("teste@example.com");
        assertThat(emailCapturado.assunto()).isEqualTo("Confirmação de Cadastro");
        assertThat(emailCapturado.mensagem()).contains("sucesso");
    }

    @Test
    @DisplayName("Deve enviar email de atualização de ciclista com sucesso")
    void deveEnviarEmailAtualizacaoCiclistaComSucesso() {
        ResponseEntity<EmailResponse> responseEntity = ResponseEntity.ok(emailResponse);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(EmailRequest.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(EmailResponse.class)).thenReturn(responseEntity);
        gateway.atualizacaoCiclistaEmail("teste@example.com");
        ArgumentCaptor<EmailRequest> captor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(requestBodyUriSpec).body(captor.capture());
        EmailRequest emailCapturado = captor.getValue();
        assertThat(emailCapturado.assunto()).isEqualTo("Atualização de Dados do Ciclista");
    }

    @Test
    @DisplayName("Deve enviar email de atualização de cartão com sucesso")
    void deveEnviarEmailAtualizacaoCartaoComSucesso() {
        ResponseEntity<EmailResponse> responseEntity = ResponseEntity.ok(emailResponse);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(EmailRequest.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(EmailResponse.class)).thenReturn(responseEntity);
        gateway.atualizacaoCartaoEmail("teste@example.com");
        ArgumentCaptor<EmailRequest> captor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(requestBodyUriSpec).body(captor.capture());
        EmailRequest emailCapturado = captor.getValue();
        assertThat(emailCapturado.assunto()).isEqualTo("Atualização de Dados do Cartão de Crédito");
    }

    @Test
    @DisplayName("Deve enviar email com dados de aluguel atual com sucesso")
    void deveEnviarEmailDadosAluguelAtualComSucesso() {
        ResponseEntity<EmailResponse> responseEntity = ResponseEntity.ok(emailResponse);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(EmailRequest.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(EmailResponse.class)).thenReturn(responseEntity);
        gateway.dadosAluguelAtualEmail("teste@example.com", aluguelResponse);
        ArgumentCaptor<EmailRequest> captor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(requestBodyUriSpec).body(captor.capture());
        EmailRequest emailCapturado = captor.getValue();
        assertThat(emailCapturado.assunto()).isEqualTo("Aluguel em Andamento");
        assertThat(emailCapturado.mensagem()).contains("já possui um aluguel em andamento");
    }

    @Test
    @DisplayName("Deve enviar email com dados de novo aluguel com sucesso")
    void deveEnviarEmailDadosAluguelNovoComSucesso() {
        ResponseEntity<EmailResponse> responseEntity = ResponseEntity.ok(emailResponse);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(EmailRequest.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(EmailResponse.class)).thenReturn(responseEntity);
        gateway.dadosAluguelNovoEmail("teste@example.com", aluguelResponse);
        ArgumentCaptor<EmailRequest> captor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(requestBodyUriSpec).body(captor.capture());
        EmailRequest emailCapturado = captor.getValue();
        assertThat(emailCapturado.assunto()).isEqualTo("Confirmação do Novo Aluguel");
        assertThat(emailCapturado.mensagem()).contains("aluguel foi iniciado com sucesso");
    }

    @Test
    @DisplayName("Deve enviar email de devolução de bicicleta com sucesso")
    void deveEnviarEmailDevolucaoBicicletaComSucesso() {
        ResponseEntity<EmailResponse> responseEntity = ResponseEntity.ok(emailResponse);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(EmailRequest.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(EmailResponse.class)).thenReturn(responseEntity);
        gateway.devolucaoBicicletaEmail("João Silva", "joao@example.com", aluguelResponse, 5.0);
        ArgumentCaptor<EmailRequest> captor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(requestBodyUriSpec).body(captor.capture());
        EmailRequest emailCapturado = captor.getValue();
        assertThat(emailCapturado.assunto()).isEqualTo("Confirmação de Devolução da Bicicleta");
        assertThat(emailCapturado.mensagem()).contains("João Silva");
        assertThat(emailCapturado.mensagem()).contains("R$ 5.0");
    }

    @Test
    @DisplayName("Deve lançar exceção quando falha ao enviar email")
    void deveLancarExcecaoQuandoFalhaEnviarEmail() {
        ResponseEntity<EmailResponse> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(EmailRequest.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(EmailResponse.class)).thenReturn(responseEntity);
        assertThatThrownBy(() -> gateway.confirmacaoCadastroEmail("teste@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Falha ao enviar email");
    }

    @Test
    @DisplayName("Deve lançar exceção quando falha ao processar cobrança")
    void deveLancarExcecaoQuandoFalhaProcessarCobranca() {
        ResponseEntity<CobrancaResponse> responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(CobrancaRequest.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(CobrancaResponse.class)).thenReturn(responseEntity);
        assertThatThrownBy(() -> gateway.cobrarAluguel(10.0, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Falha ao processar a cobrança do aluguel!");
    }

    @Test
    @DisplayName("Deve validar cartão de crédito com sucesso")
    void deveValidarCartaoDeCreditoComSucesso() {
        AdicionarCartaoRequest cartaoRequest = new AdicionarCartaoRequest(
                "João Silva", "1234567890123456", LocalDate.of(2030, 01, 01), "123"
        );
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(ValidacaoCartaoRequest.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(Void.class)).thenReturn(responseEntity);
        gateway.validacaoCartaoDeCredito(cartaoRequest);
        verify(requestBodyUriSpec).body(any(ValidacaoCartaoRequest.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando falha ao validar cartão de crédito")
    void deveLancarExcecaoQuandoFalhaValidarCartao() {
        AdicionarCartaoRequest cartaoRequest = new AdicionarCartaoRequest(
                "João Silva", "1234567890123456", LocalDate.of(2030, 01, 01), "123"
        );
        ResponseEntity<Void> responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(ValidacaoCartaoRequest.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(Void.class)).thenReturn(responseEntity);
        assertThatThrownBy(() -> gateway.validacaoCartaoDeCredito(cartaoRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Validação do cartão de crédito falhou!");
    }
}

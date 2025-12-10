package com.es2.microservicos.external.gateways;

import com.es2.microservicos.dtos.responses.BicicletaResponse;
import com.es2.microservicos.dtos.responses.TrancaResponse;
import com.es2.microservicos.external.domain.BicicletaStatus;
import com.es2.microservicos.external.domain.TrancaStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EquipamentoServiceGateway")
class EquipamentoServiceGatewayTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private EquipamentoServiceGateway gateway;

    private BicicletaResponse bicicletaResponse;
    private TrancaResponse trancaResponse;

    @BeforeEach
    void setUp() {
        gateway = new EquipamentoServiceGateway(restClient);
        bicicletaResponse = new BicicletaResponse(
                1L, "Bike001", "Caloi", "2023", 123, BicicletaStatus.DISPONIVEL
        );
        trancaResponse = new TrancaResponse(1L, 1L, 123, "Tranca 1", "2023", "Modelo X", "DESTRANCAR");
    }

    @Test
    @DisplayName("Deve lançar exceção quando bicicleta não encontrada por ID")
    void deveLancarExcecaoQuandoBicicletaNaoEncontradaPorId() {
        ResponseEntity<BicicletaResponse> responseEntity = ResponseEntity.ok().build();
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(BicicletaResponse.class)).thenReturn(responseEntity);
        assertThatThrownBy(() -> gateway.obterBicicletaPorId(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Não existe bicicleta com ID: 1");
    }

    @Test
    @DisplayName("Deve obter bicicleta por ID da tranca com sucesso")
    void deveObterBicicletaPorIdTrancaComSucesso() {
        ResponseEntity<BicicletaResponse> responseEntity = ResponseEntity.ok(bicicletaResponse);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(BicicletaResponse.class)).thenReturn(responseEntity);
        BicicletaResponse resultado = gateway.obterBicicletaPorIdTranca(1L);
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        verify(restClient).get();
    }

    @Test
    @DisplayName("Deve lançar exceção quando não existe bicicleta na tranca")
    void deveLancarExcecaoQuandoNaoExisteBicicletaNaTranca() {
        ResponseEntity<BicicletaResponse> responseEntity = ResponseEntity.ok().build();
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(BicicletaResponse.class)).thenReturn(responseEntity);
        assertThatThrownBy(() -> gateway.obterBicicletaPorIdTranca(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Não existe bicicleta na tranca com ID: 1");
    }

    @Test
    @DisplayName("Deve verificar existência de tranca com sucesso")
    void deveVerificarExistenciaTrancaComSucesso() {
        ResponseEntity<Boolean> responseEntity = ResponseEntity.ok(true);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(Boolean.class)).thenReturn(responseEntity);
        boolean resultado = gateway.existeTrancaPorId(1L);
        assertThat(resultado).isTrue();
        verify(restClient).get();
    }

    @Test
    @DisplayName("Deve retornar false quando tranca não existe")
    void deveRetornarFalseQuandoTrancaNaoExiste() {
        ResponseEntity<Boolean> responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(Boolean.class)).thenReturn(responseEntity);
        boolean resultado = gateway.existeTrancaPorId(1L);
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID da tranca é nulo")
    void deveLancarExcecaoQuandoIdTrancaNulo() {
        assertThatThrownBy(() -> gateway.existeTrancaPorId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID da tranca não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve alterar status da bicicleta com sucesso")
    void deveAlterarStatusBicicletaComSucesso() {
        BicicletaResponse bicicletaAtualizada = new BicicletaResponse(
                1L, "Bike001", "Caloi", "2023", 123, BicicletaStatus.EM_USO
        );
        ResponseEntity<BicicletaResponse> responseEntity = ResponseEntity.ok(bicicletaAtualizada);
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(BicicletaResponse.class)).thenReturn(responseEntity);
        BicicletaResponse resultado = gateway.alterarStatusBicicleta(1L, BicicletaStatus.EM_USO);
        assertThat(resultado).isNotNull();
        assertThat(resultado.status()).isEqualTo(BicicletaStatus.EM_USO);
        verify(restClient).put();
    }

    @Test
    @DisplayName("Deve lançar exceção quando falha ao alterar status da bicicleta")
    void deveLancarExcecaoQuandoFalhaAlterarStatusBicicleta() {
        ResponseEntity<BicicletaResponse> responseEntity = ResponseEntity.ok().build();
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(BicicletaResponse.class)).thenReturn(responseEntity);
        assertThatThrownBy(() -> gateway.alterarStatusBicicleta(1L, BicicletaStatus.EM_USO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Não foi possível alterar o status da bicicleta com ID: 1");
    }

    @Test
    @DisplayName("Deve alterar status da tranca com sucesso")
    void deveAlterarStatusTrancaComSucesso() {
        ResponseEntity<TrancaResponse> responseEntity = ResponseEntity.ok(trancaResponse);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(TrancaResponse.class)).thenReturn(responseEntity);
        TrancaResponse resultado = gateway.alterarStatusTranca(1L, TrancaStatus.TRANCAR);
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        verify(restClient).post();
    }

    @Test
    @DisplayName("Deve lançar exceção quando falha ao alterar status da tranca")
    void deveLancarExcecaoQuandoFalhaAlterarStatusTranca() {
        ResponseEntity<TrancaResponse> responseEntity = ResponseEntity.ok().build();
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(TrancaResponse.class)).thenReturn(responseEntity);
        assertThatThrownBy(() -> gateway.alterarStatusTranca(1L, TrancaStatus.TRANCAR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Não foi possível alterar o status da tranca com ID: 1");
    }
}

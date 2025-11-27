package com.es2.microservicos.external.gateways;

import com.es2.microservicos.dtos.responses.BicicletaResponse;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para EquipamentoServiceGateway")
class EquipamentoServiceGatewayTest {

    @Mock
    private RestClient restClient;

    private EquipamentoServiceGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = new EquipamentoServiceGateway(restClient);
    }

    @Test
    @DisplayName("Deve retornar OK ao verificar existência de tranca")
    void deveRetornarOkAoVerificarExistenciaDeTranca() {
        ResponseEntity<Boolean> response = gateway.existeTrancaPorId(123L);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar response sem body ao obter bicicleta por ID de tranca")
    void deveRetornarResponseSemBodyAoObterBicicletaPorIdTranca() {
        ResponseEntity<BicicletaResponse> response = gateway.obterBicicletaPorIdTranca(100L);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.hasBody());
    }

    @Test
    @DisplayName("Deve retornar OK ao alterar status da bicicleta")
    void deveRetornarOkAoAlterarStatusBicicleta() {
        ResponseEntity response = gateway.alterarStatusBicicleta(50L, BicicletaStatus.EM_USO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar OK ao alterar status da tranca")
    void deveRetornarOkAoAlterarStatusTranca() {
        ResponseEntity response = gateway.alterarStatusTranca(200L, TrancaStatus.DESTRANCAR);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar response sem body ao obter bicicleta por ID")
    void deveRetornarResponseSemBodyAoObterBicicletaPorId() {
        ResponseEntity<BicicletaResponse> response = gateway.obterBicicletaPorId(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.hasBody());
    }

    @Test
    @DisplayName("Deve processar verificação de múltiplas trancas")
    void deveProcessarVerificacaoMultiplasTrancas() {
        ResponseEntity<Boolean> response1 = gateway.existeTrancaPorId(1L);
        ResponseEntity<Boolean> response2 = gateway.existeTrancaPorId(10L);
        ResponseEntity<Boolean> response3 = gateway.existeTrancaPorId(100L);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    @DisplayName("Deve processar alteração de status para diferentes bicicletas")
    void deveProcessarAlteracaoStatusDiferentesBicicletas() {
        ResponseEntity response1 = gateway.alterarStatusBicicleta(1L, BicicletaStatus.DISPONIVEL);
        ResponseEntity response2 = gateway.alterarStatusBicicleta(2L, BicicletaStatus.EM_USO);
        ResponseEntity response3 = gateway.alterarStatusBicicleta(3L, BicicletaStatus.EM_REPARO);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    @DisplayName("Deve processar alteração de status para diferentes trancas")
    void deveProcessarAlteracaoStatusDiferentesTrancas() {
        ResponseEntity response1 = gateway.alterarStatusTranca(1L, TrancaStatus.DESTRANCAR);
        ResponseEntity response2 = gateway.alterarStatusTranca(2L, TrancaStatus.TRANCAR);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    @DisplayName("Deve processar busca com IDs negativos")
    void deveProcessarBuscaComIdsNegativos() {
        ResponseEntity<Boolean> response1 = gateway.existeTrancaPorId(-1L);
        ResponseEntity<BicicletaResponse> response2 = gateway.obterBicicletaPorId(-5L);
        ResponseEntity<BicicletaResponse> response3 = gateway.obterBicicletaPorIdTranca(-10L);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        assertFalse(response2.hasBody());
        assertFalse(response3.hasBody());
    }

    @Test
    @DisplayName("Deve processar busca com ID zero")
    void deveProcessarBuscaComIdZero() {
        ResponseEntity<Boolean> trancaExiste = gateway.existeTrancaPorId(0L);
        ResponseEntity<BicicletaResponse> bicicleta = gateway.obterBicicletaPorId(0L);

        assertEquals(HttpStatus.OK, trancaExiste.getStatusCode());
        assertEquals(HttpStatus.OK, bicicleta.getStatusCode());
        assertFalse(bicicleta.hasBody());
    }

    @Test
    @DisplayName("Deve processar chamadas sequenciais para mesmo ID")
    void deveProcessarChamadasSequenciaisParaMesmoId() {
        Long idTranca = 50L;

        ResponseEntity<Boolean> check1 = gateway.existeTrancaPorId(idTranca);
        ResponseEntity<Boolean> check2 = gateway.existeTrancaPorId(idTranca);
        ResponseEntity<BicicletaResponse> bicicleta1 = gateway.obterBicicletaPorIdTranca(idTranca);
        ResponseEntity<BicicletaResponse> bicicleta2 = gateway.obterBicicletaPorIdTranca(idTranca);

        assertEquals(HttpStatus.OK, check1.getStatusCode());
        assertEquals(HttpStatus.OK, check2.getStatusCode());
        assertEquals(HttpStatus.OK, bicicleta1.getStatusCode());
        assertEquals(HttpStatus.OK, bicicleta2.getStatusCode());
        assertFalse(bicicleta1.hasBody());
        assertFalse(bicicleta2.hasBody());
    }

    @Test
    @DisplayName("Deve retornar response válido para todos os métodos")
    void deveRetornarResponseValidoParaTodosMetodos() {
        ResponseEntity<Boolean> existe = gateway.existeTrancaPorId(1L);
        ResponseEntity<BicicletaResponse> bicicleta1 = gateway.obterBicicletaPorId(1L);
        ResponseEntity<BicicletaResponse> bicicleta2 = gateway.obterBicicletaPorIdTranca(1L);
        ResponseEntity statusBike = gateway.alterarStatusBicicleta(1L, BicicletaStatus.DISPONIVEL);
        ResponseEntity statusTranca = gateway.alterarStatusTranca(1L, TrancaStatus.DESTRANCAR);

        assertAll(
                () -> assertNotNull(existe),
                () -> assertNotNull(bicicleta1),
                () -> assertNotNull(bicicleta2),
                () -> assertNotNull(statusBike),
                () -> assertNotNull(statusTranca),
                () -> assertEquals(HttpStatus.OK, existe.getStatusCode()),
                () -> assertEquals(HttpStatus.OK, bicicleta1.getStatusCode()),
                () -> assertEquals(HttpStatus.OK, bicicleta2.getStatusCode()),
                () -> assertEquals(HttpStatus.OK, statusBike.getStatusCode()),
                () -> assertEquals(HttpStatus.OK, statusTranca.getStatusCode())
        );
    }

    @Test
    @DisplayName("Deve processar múltiplos IDs diferentes em sequência")
    void deveProcessarMultiplosIdsDiferentesEmSequencia() {
        for (long i = 1; i <= 5; i++) {
            ResponseEntity<Boolean> response = gateway.existeTrancaPorId(i);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    @DisplayName("Deve aceitar todos os status de bicicleta")
    void deveAceitarTodosStatusDeBicicleta() {
        assertDoesNotThrow(() -> {
            gateway.alterarStatusBicicleta(1L, BicicletaStatus.DISPONIVEL);
            gateway.alterarStatusBicicleta(1L, BicicletaStatus.EM_USO);
            gateway.alterarStatusBicicleta(1L, BicicletaStatus.EM_REPARO);
            gateway.alterarStatusBicicleta(1L, BicicletaStatus.REPARO_SOLICITADO);
            gateway.alterarStatusBicicleta(1L, BicicletaStatus.APOSENTADA);
            gateway.alterarStatusBicicleta(1L, BicicletaStatus.NOVA);
        });
    }

    @Test
    @DisplayName("Deve aceitar todos os status de tranca")
    void deveAceitarTodosStatusDeTranca() {
        assertDoesNotThrow(() -> {
            gateway.alterarStatusTranca(1L, TrancaStatus.TRANCAR);
            gateway.alterarStatusTranca(1L, TrancaStatus.DESTRANCAR);
        });
    }
}

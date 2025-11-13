package com.es2.microservicos.gateways;

import com.es2.microservicos.dtos.responses.BicicletaResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para EquipamentoServiceGateway")
class EquipamentoServiceGatewayTest {

    @Mock
    private RestClient restClient;

    @InjectMocks
    private EquipamentoServiceGateway equipamentoServiceGateway;

    @Test
    @DisplayName("Deve retornar null ao obter bicicleta por ID de ciclista")
    void deveRetornarNullAoObterBicicletaPorIdCiclista() {
        BicicletaResponse resultado = equipamentoServiceGateway.obterBicicletaPorId(1L);

        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve retornar null ao obter bicicleta por ID de tranca")
    void deveRetornarNullAoObterBicicletaPorIdTranca() {
        BicicletaResponse resultado = equipamentoServiceGateway.obterBicicletaPorIdTranca(100L);

        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve retornar true quando tranca existe")
    void deveRetornarTrueQuandoTrancaExiste() {
        boolean resultado = equipamentoServiceGateway.existeTrancaPorId(1L);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve verificar existência de tranca com ID válido")
    void deveVerificarExistenciaDeTrancaComIdValido() {
        boolean resultado = equipamentoServiceGateway.existeTrancaPorId(999L);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve processar busca de bicicleta por múltiplos IDs de ciclista")
    void deveProcessarBuscaPorMultiplosIdsCiclista() {
        BicicletaResponse resultado1 = equipamentoServiceGateway.obterBicicletaPorId(1L);
        BicicletaResponse resultado2 = equipamentoServiceGateway.obterBicicletaPorId(2L);
        BicicletaResponse resultado3 = equipamentoServiceGateway.obterBicicletaPorId(3L);

        assertNull(resultado1);
        assertNull(resultado2);
        assertNull(resultado3);
    }

    @Test
    @DisplayName("Deve processar busca de bicicleta por múltiplos IDs de tranca")
    void deveProcessarBuscaPorMultiplosIdsTranca() {
        BicicletaResponse resultado1 = equipamentoServiceGateway.obterBicicletaPorIdTranca(100L);
        BicicletaResponse resultado2 = equipamentoServiceGateway.obterBicicletaPorIdTranca(200L);
        BicicletaResponse resultado3 = equipamentoServiceGateway.obterBicicletaPorIdTranca(300L);

        assertNull(resultado1);
        assertNull(resultado2);
        assertNull(resultado3);
    }

    @Test
    @DisplayName("Deve verificar existência de múltiplas trancas")
    void deveVerificarExistenciaDeMultiplasTrancas() {
        boolean resultado1 = equipamentoServiceGateway.existeTrancaPorId(1L);
        boolean resultado2 = equipamentoServiceGateway.existeTrancaPorId(10L);
        boolean resultado3 = equipamentoServiceGateway.existeTrancaPorId(100L);

        assertTrue(resultado1);
        assertTrue(resultado2);
        assertTrue(resultado3);
    }

    @Test
    @DisplayName("Deve processar busca com ID zero")
    void deveProcessarBuscaComIdZero() {
        BicicletaResponse resultado = equipamentoServiceGateway.obterBicicletaPorId(0L);

        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve processar busca de tranca com ID zero")
    void deveProcessarBuscaDeTrancaComIdZero() {
        BicicletaResponse resultado = equipamentoServiceGateway.obterBicicletaPorIdTranca(0L);

        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve verificar existência de tranca com ID zero")
    void deveVerificarExistenciaDeTrancaComIdZero() {
        boolean resultado = equipamentoServiceGateway.existeTrancaPorId(0L);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve processar busca com IDs negativos")
    void deveProcessarBuscaComIdsNegativos() {
        BicicletaResponse resultado1 = equipamentoServiceGateway.obterBicicletaPorId(-1L);
        BicicletaResponse resultado2 = equipamentoServiceGateway.obterBicicletaPorIdTranca(-5L);
        boolean resultado3 = equipamentoServiceGateway.existeTrancaPorId(-10L);

        assertNull(resultado1);
        assertNull(resultado2);
        assertTrue(resultado3);
    }

    @Test
    @DisplayName("Deve processar chamadas sequenciais para mesmo ID")
    void deveProcessarChamadasSequenciaisParaMesmoId() {
        Long idCiclista = 5L;
        Long idTranca = 50L;

        BicicletaResponse resultado1 = equipamentoServiceGateway.obterBicicletaPorId(idCiclista);
        BicicletaResponse resultado2 = equipamentoServiceGateway.obterBicicletaPorId(idCiclista);
        BicicletaResponse resultado3 = equipamentoServiceGateway.obterBicicletaPorIdTranca(idTranca);
        BicicletaResponse resultado4 = equipamentoServiceGateway.obterBicicletaPorIdTranca(idTranca);
        boolean resultado5 = equipamentoServiceGateway.existeTrancaPorId(idTranca);
        boolean resultado6 = equipamentoServiceGateway.existeTrancaPorId(idTranca);

        assertNull(resultado1);
        assertNull(resultado2);
        assertNull(resultado3);
        assertNull(resultado4);
        assertTrue(resultado5);
        assertTrue(resultado6);
    }
}

package com.es2.microservicos.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para Aluguel")
class AluguelTest {

    private Ciclista ciclista;
    private Aluguel aluguel;

    @BeforeEach
    void setUp() {
        ciclista = new Ciclista();
        ciclista.setId(1L);
        ciclista.setNome("João Silva");
        ciclista.setEmail("joao@email.com");

        aluguel = new Aluguel();
    }

    @Test
    @DisplayName("Deve criar aluguel com construtor completo")
    void deveCriarAluguelComConstrutorCompleto() {
        LocalDateTime horaInicio = LocalDateTime.now();
        LocalDateTime horaFim = LocalDateTime.now().plusHours(2);

        Aluguel aluguelCompleto = new Aluguel(
                50L,
                ciclista,
                horaInicio,
                horaFim,
                10.0,
                100L,
                200L
        );

        assertNotNull(aluguelCompleto);
        assertEquals(50L, aluguelCompleto.getBicicletaId());
        assertEquals(ciclista, aluguelCompleto.getCiclista());
        assertEquals(horaInicio, aluguelCompleto.getHoraInicio());
        assertEquals(horaFim, aluguelCompleto.getHoraFim());
        assertEquals(10.0, aluguelCompleto.getCobranca());
        assertEquals(100L, aluguelCompleto.getTrancaInicio());
        assertEquals(200L, aluguelCompleto.getTrancaFim());
    }

    @Test
    @DisplayName("Deve criar aluguel com construtor vazio")
    void deveCriarAluguelComConstrutorVazio() {
        assertNotNull(aluguel);
        assertNull(aluguel.getId());
        assertNull(aluguel.getBicicletaId());
        assertNull(aluguel.getCiclista());
    }

    @Test
    @DisplayName("Deve retornar valor inicial de aluguel")
    void deveRetornarValorInicialDeAluguel() {
        double valorInicial = Aluguel.valorInicialAluguel();
        assertEquals(10.0, valorInicial);
    }

    @Test
    @DisplayName("Deve definir e obter ID")
    void deveDefinirEObterID() {
        aluguel.setId(10L);
        assertEquals(10L, aluguel.getId());
    }

    @Test
    @DisplayName("Deve definir e obter bicicleta ID")
    void deveDefinirEObterBicicletaId() {
        aluguel.setBicicletaId(50L);
        assertEquals(50L, aluguel.getBicicletaId());
    }

    @Test
    @DisplayName("Deve definir e obter ciclista")
    void deveDefinirEObterCiclista() {
        aluguel.setCiclista(ciclista);
        assertEquals(ciclista, aluguel.getCiclista());
        assertEquals("João Silva", aluguel.getCiclista().getNome());
    }

    @Test
    @DisplayName("Deve definir e obter hora de início")
    void deveDefinirEObterHoraInicio() {
        LocalDateTime horaInicio = LocalDateTime.now();
        aluguel.setHoraInicio(horaInicio);
        assertEquals(horaInicio, aluguel.getHoraInicio());
    }

    @Test
    @DisplayName("Deve definir e obter hora de fim")
    void deveDefinirEObterHoraFim() {
        LocalDateTime horaFim = LocalDateTime.now();
        aluguel.setHoraFim(horaFim);
        assertEquals(horaFim, aluguel.getHoraFim());
    }

    @Test
    @DisplayName("Deve definir e obter cobrança")
    void deveDefinirEObterCobranca() {
        aluguel.setCobranca(25.50);
        assertEquals(25.50, aluguel.getCobranca());
    }

    @Test
    @DisplayName("Deve definir e obter tranca início")
    void deveDefinirEObterTrancaInicio() {
        aluguel.setTrancaInicio(100L);
        assertEquals(100L, aluguel.getTrancaInicio());
    }

    @Test
    @DisplayName("Deve definir e obter tranca fim")
    void deveDefinirEObterTrancaFim() {
        aluguel.setTrancaFim(200L);
        assertEquals(200L, aluguel.getTrancaFim());
    }

    @Test
    @DisplayName("Deve permitir valores nulos para tranca fim")
    void devePermitirValoresNulosParaTrancaFim() {
        aluguel.setTrancaFim(null);
        assertNull(aluguel.getTrancaFim());
    }

    @Test
    @DisplayName("Deve permitir valores nulos para hora fim")
    void devePermitirValoresNulosParaHoraFim() {
        aluguel.setHoraFim(null);
        assertNull(aluguel.getHoraFim());
    }

    @Test
    @DisplayName("Deve criar aluguel em andamento sem hora fim")
    void deveCriarAluguelEmAndamentoSemHoraFim() {
        aluguel.setBicicletaId(50L);
        aluguel.setCiclista(ciclista);
        aluguel.setHoraInicio(LocalDateTime.now());
        aluguel.setTrancaInicio(100L);
        aluguel.setCobranca(10.0);

        assertNotNull(aluguel.getHoraInicio());
        assertNull(aluguel.getHoraFim());
        assertNull(aluguel.getTrancaFim());
    }
}

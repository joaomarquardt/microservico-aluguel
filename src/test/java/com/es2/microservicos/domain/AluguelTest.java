package com.es2.microservicos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para Aluguel")
class AluguelTest {

    @Test
    @DisplayName("Deve criar aluguel com construtor vazio")
    void deveCriarAluguelComConstrutorVazio() {
        Aluguel aluguel = new Aluguel();
        assertNotNull(aluguel);
    }

    @Test
    @DisplayName("Deve criar aluguel com construtor completo com ID")
    void deveCriarAluguelComConstrutorCompletoComId() {
        Ciclista ciclista = new Ciclista();
        ciclista.setId(1L);
        LocalDateTime agora = LocalDateTime.now();

        Aluguel aluguel = new Aluguel(
                1L,
                50L,
                ciclista,
                agora,
                null,
                100,
                100L,
                0L
        );

        assertEquals(1L, aluguel.getId());
        assertEquals(50L, aluguel.getBicicletaId());
        assertEquals(ciclista, aluguel.getCiclista());
        assertEquals(agora, aluguel.getHoraInicio());
        assertNull(aluguel.getHoraFim());
        assertEquals(100, aluguel.getCobranca());
        assertEquals(100L, aluguel.getTrancaInicio());
        assertEquals(0L, aluguel.getTrancaFim());
    }

    @Test
    @DisplayName("Deve criar aluguel com construtor sem ID")
    void deveCriarAluguelComConstrutorSemId() {
        Ciclista ciclista = new Ciclista();
        ciclista.setId(1L);
        LocalDateTime agora = LocalDateTime.now();

        Aluguel aluguel = new Aluguel(
                50L,
                ciclista,
                agora,
                null,
                100,
                100L,
                0L
        );

        assertNull(aluguel.getId());
        assertEquals(50L, aluguel.getBicicletaId());
        assertEquals(ciclista, aluguel.getCiclista());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos")
    void deveDefinirEObterTodosCampos() {
        Aluguel aluguel = new Aluguel();
        Ciclista ciclista = new Ciclista();
        ciclista.setId(1L);
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime fim = agora.plusHours(2);

        aluguel.setId(1L);
        aluguel.setBicicletaId(50L);
        aluguel.setCiclista(ciclista);
        aluguel.setHoraInicio(agora);
        aluguel.setHoraFim(fim);
        aluguel.setCobranca(100);
        aluguel.setTrancaInicio(100L);
        aluguel.setTrancaFim(200L);

        assertEquals(1L, aluguel.getId());
        assertEquals(50L, aluguel.getBicicletaId());
        assertEquals(ciclista, aluguel.getCiclista());
        assertEquals(agora, aluguel.getHoraInicio());
        assertEquals(fim, aluguel.getHoraFim());
        assertEquals(100, aluguel.getCobranca());
        assertEquals(100L, aluguel.getTrancaInicio());
        assertEquals(200L, aluguel.getTrancaFim());
    }
}

package com.es2.microservicos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para CartaoDeCredito")
class CartaoDeCreditoTest {

    @Test
    @DisplayName("Deve criar cartão com construtor vazio")
    void deveCriarCartaoComConstrutorVazio() {
        CartaoDeCredito cartao = new CartaoDeCredito();
        assertNotNull(cartao);
    }

    @Test
    @DisplayName("Deve criar cartão com construtor completo")
    void deveCriarCartaoComConstrutorCompleto() {
        Ciclista ciclista = new Ciclista();
        ciclista.setId(1L);

        CartaoDeCredito cartao = new CartaoDeCredito(
                1L,
                "João Silva",
                "1234567890123456",
                LocalDate.of(2027, 12, 31),
                "123",
                ciclista
        );

        assertEquals(1L, cartao.getId());
        assertEquals("João Silva", cartao.getNomeTitular());
        assertEquals("1234567890123456", cartao.getNumeroCartao());
        assertEquals(LocalDate.of(2027, 12, 31), cartao.getDataValidade());
        assertEquals("123", cartao.getCodigoSeguranca());
        assertEquals(ciclista, cartao.getCiclista());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos")
    void deveDefinirEObterTodosCampos() {
        CartaoDeCredito cartao = new CartaoDeCredito();
        Ciclista ciclista = new Ciclista();
        ciclista.setId(1L);

        cartao.setId(1L);
        cartao.setNomeTitular("João Silva");
        cartao.setNumeroCartao("1234567890123456");
        cartao.setDataValidade(LocalDate.of(2027, 12, 31));
        cartao.setCodigoSeguranca("123");
        cartao.setCiclista(ciclista);

        assertEquals(1L, cartao.getId());
        assertEquals("João Silva", cartao.getNomeTitular());
        assertEquals("1234567890123456", cartao.getNumeroCartao());
        assertEquals(LocalDate.of(2027, 12, 31), cartao.getDataValidade());
        assertEquals("123", cartao.getCodigoSeguranca());
        assertEquals(ciclista, cartao.getCiclista());
    }
}

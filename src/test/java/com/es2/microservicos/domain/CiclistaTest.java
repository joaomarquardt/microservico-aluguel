package com.es2.microservicos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para Ciclista")
class CiclistaTest {

    @Test
    @DisplayName("Deve criar ciclista com construtor vazio")
    void deveCriarCiclistaComConstrutorVazio() {
        Ciclista ciclista = new Ciclista();
        assertNotNull(ciclista);
    }

    @Test
    @DisplayName("Deve criar ciclista com construtor completo")
    void deveCriarCiclistaComConstrutorCompleto() {
        Passaporte passaporte = new Passaporte("ABC123", LocalDate.of(2030, 12, 31), "Brasil");

        Ciclista ciclista = new Ciclista(
                1L,
                "João Silva",
                "joao@email.com",
                Status.ATIVO,
                "12345678901",
                LocalDate.of(1990, 1, 1),
                Nacionalidade.BRASILEIRO,
                passaporte,
                "http://foto.com/doc.jpg",
                "123"
        );

        assertEquals(1L, ciclista.getId());
        assertEquals("João Silva", ciclista.getNome());
        assertEquals("joao@email.com", ciclista.getEmail());
        assertEquals(Status.ATIVO, ciclista.getStatus());
        assertEquals("12345678901", ciclista.getCpf());
        assertEquals(LocalDate.of(1990, 1, 1), ciclista.getNascimento());
        assertEquals(Nacionalidade.BRASILEIRO, ciclista.getNacionalidade());
        assertEquals(passaporte, ciclista.getPassaporte());
        assertEquals("http://foto.com/doc.jpg", ciclista.getUrlFotoDocumento());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos")
    void deveDefinirEObterTodosCampos() {
        Ciclista ciclista = new Ciclista();
        Passaporte passaporte = new Passaporte("ABC123", LocalDate.of(2030, 12, 31), "Brasil");

        ciclista.setId(1L);
        ciclista.setNome("João Silva");
        ciclista.setEmail("joao@email.com");
        ciclista.setStatus(Status.ATIVO);
        ciclista.setCpf("12345678901");
        ciclista.setNascimento(LocalDate.of(1990, 1, 1));
        ciclista.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclista.setPassaporte(passaporte);
        ciclista.setUrlFotoDocumento("http://foto.com/doc.jpg");

        assertEquals(1L, ciclista.getId());
        assertEquals("João Silva", ciclista.getNome());
        assertEquals("joao@email.com", ciclista.getEmail());
        assertEquals(Status.ATIVO, ciclista.getStatus());
        assertEquals("12345678901", ciclista.getCpf());
        assertEquals(LocalDate.of(1990, 1, 1), ciclista.getNascimento());
        assertEquals(Nacionalidade.BRASILEIRO, ciclista.getNacionalidade());
        assertEquals(passaporte, ciclista.getPassaporte());
        assertEquals("http://foto.com/doc.jpg", ciclista.getUrlFotoDocumento());
    }
}


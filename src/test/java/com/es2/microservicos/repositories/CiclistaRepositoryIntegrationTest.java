package com.es2.microservicos.repositories;

import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Nacionalidade;
import com.es2.microservicos.domain.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes de Integração - CiclistaRepository")
class CiclistaRepositoryIntegrationTest {

    @Autowired
    private CiclistaRepository ciclistaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Ciclista ciclista;

    @BeforeEach
    void setUp() {
        ciclista = new Ciclista();
        ciclista.setNome("João Silva");
        ciclista.setEmail("joao@test.com");
        ciclista.setCpf("12345678900");
        ciclista.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclista.setNascimento(LocalDate.of(1990, 1, 1));
        ciclista.setUrlFotoDocumento("http://foto.com");
        ciclista.setStatus(Status.ATIVO);
    }

    @Test
    @DisplayName("Deve salvar ciclista no banco de dados")
    void deveSalvarCiclistaNoBancoDeDados() {
        Ciclista ciclistaSalvo = ciclistaRepository.save(ciclista);
        assertThat(ciclistaSalvo.getId()).isNotNull();
        assertThat(ciclistaSalvo.getNome()).isEqualTo("João Silva");
        assertThat(ciclistaSalvo.getEmail()).isEqualTo("joao@test.com");
    }

    @Test
    @DisplayName("Deve verificar se email já existe")
    void deveVerificarSeEmailJaExiste() {
        ciclistaRepository.save(ciclista);
        entityManager.flush();
        boolean existe = ciclistaRepository.existsByEmail("joao@test.com");
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando email não existe")
    void deveRetornarFalseQuandoEmailNaoExiste() {
        boolean existe = ciclistaRepository.existsByEmail("naoexiste@test.com");
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve buscar ciclista por ID")
    void deveBuscarCiclistaPorId() {
        Ciclista ciclistaSalvo = ciclistaRepository.save(ciclista);
        entityManager.flush();
        entityManager.clear();
        Optional<Ciclista> resultado = ciclistaRepository.findById(ciclistaSalvo.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve atualizar status do ciclista")
    void deveAtualizarStatusDoCiclista() {
        Ciclista ciclistaSalvo = ciclistaRepository.save(ciclista);
        entityManager.flush();
        ciclistaSalvo.setStatus(Status.INATIVO);
        ciclistaRepository.save(ciclistaSalvo);
        entityManager.flush();
        entityManager.clear();
        Ciclista ciclistaAtualizado = ciclistaRepository.findById(ciclistaSalvo.getId()).orElseThrow();
        assertThat(ciclistaAtualizado.getStatus()).isEqualTo(Status.INATIVO);
    }

    @Test
    @DisplayName("Deve atualizar informações do ciclista")
    void deveAtualizarInformacoesDoCiclista() {
        Ciclista ciclistaSalvo = ciclistaRepository.save(ciclista);
        entityManager.flush();
        ciclistaSalvo.setNome("João Silva Atualizado");
        ciclistaSalvo.setEmail("joao.atualizado@test.com");
        ciclistaRepository.save(ciclistaSalvo);
        entityManager.flush();
        entityManager.clear();
        Ciclista ciclistaAtualizado = ciclistaRepository.findById(ciclistaSalvo.getId()).orElseThrow();
        assertThat(ciclistaAtualizado.getNome()).isEqualTo("João Silva Atualizado");
        assertThat(ciclistaAtualizado.getEmail()).isEqualTo("joao.atualizado@test.com");
    }
}

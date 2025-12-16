package com.es2.microservicos.repositories;

import com.es2.microservicos.domain.CartaoDeCredito;
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
@DisplayName("Testes de Integração - CartaoDeCreditoRepository")
class CartaoDeCreditoRepositoryIntegrationTest {

    @Autowired
    private CartaoDeCreditoRepository cartaoDeCreditoRepository;

    @Autowired
    private CiclistaRepository ciclistaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Ciclista ciclista;
    private CartaoDeCredito cartaoDeCredito;

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
        ciclista = ciclistaRepository.save(ciclista);
        cartaoDeCredito = new CartaoDeCredito();
        cartaoDeCredito.setNomeTitular("João Silva");
        cartaoDeCredito.setNumero("1234567890123456");
        cartaoDeCredito.setValidade(LocalDate.of(2025, 12, 31));
        cartaoDeCredito.setCvv("123");
        cartaoDeCredito.setCiclista(ciclista);
    }

    @Test
    @DisplayName("Deve salvar cartão de crédito no banco de dados")
    void deveSalvarCartaoDeCreditoNoBancoDeDados() {
        CartaoDeCredito cartaoSalvo = cartaoDeCreditoRepository.save(cartaoDeCredito);
        assertThat(cartaoSalvo.getId()).isNotNull();
        assertThat(cartaoSalvo.getNomeTitular()).isEqualTo("João Silva");
        assertThat(cartaoSalvo.getNumero()).isEqualTo("1234567890123456");
    }

    @Test
    @DisplayName("Deve buscar cartão por ID do ciclista")
    void deveBuscarCartaoPorIdDoCiclista() {
        cartaoDeCreditoRepository.save(cartaoDeCredito);
        entityManager.flush();
        Optional<CartaoDeCredito> resultado = cartaoDeCreditoRepository.findByCiclistaId(ciclista.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNomeTitular()).isEqualTo("João Silva");
        assertThat(resultado.get().getCiclista().getId()).isEqualTo(ciclista.getId());
    }

    @Test
    @DisplayName("Deve retornar vazio quando cartão não existe para o ciclista")
    void deveRetornarVazioQuandoCartaoNaoExisteParaCiclista() {
        Optional<CartaoDeCredito> resultado = cartaoDeCreditoRepository.findByCiclistaId(999L);
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve manter relacionamento entre Cartão e Ciclista")
    void deveManterRelacionamentoEntreCartaoECiclista() {
        CartaoDeCredito cartaoSalvo = cartaoDeCreditoRepository.save(cartaoDeCredito);
        entityManager.flush();
        entityManager.clear();
        CartaoDeCredito cartaoBuscado = cartaoDeCreditoRepository.findById(cartaoSalvo.getId()).orElseThrow();
        assertThat(cartaoBuscado.getCiclista()).isNotNull();
        assertThat(cartaoBuscado.getCiclista().getNome()).isEqualTo("João Silva");
        assertThat(cartaoBuscado.getCiclista().getEmail()).isEqualTo("joao@test.com");
    }

    @Test
    @DisplayName("Deve atualizar informações do cartão")
    void deveAtualizarInformacoesDoCartao() {
        CartaoDeCredito cartaoSalvo = cartaoDeCreditoRepository.save(cartaoDeCredito);
        entityManager.flush();
        cartaoSalvo.setNomeTitular("João Silva Atualizado");
        cartaoSalvo.setNumero("9876543210987654");
        cartaoDeCreditoRepository.save(cartaoSalvo);
        entityManager.flush();
        entityManager.clear();
        CartaoDeCredito cartaoAtualizado = cartaoDeCreditoRepository.findById(cartaoSalvo.getId()).orElseThrow();
        assertThat(cartaoAtualizado.getNomeTitular()).isEqualTo("João Silva Atualizado");
        assertThat(cartaoAtualizado.getNumero()).isEqualTo("9876543210987654");
    }

    @Test
    @DisplayName("Deve deletar cartão do banco de dados")
    void deveDeletarCartaoDoBancoDeDados() {
        CartaoDeCredito cartaoSalvo = cartaoDeCreditoRepository.save(cartaoDeCredito);
        entityManager.flush();
        Long cartaoId = cartaoSalvo.getId();
        cartaoDeCreditoRepository.delete(cartaoSalvo);
        entityManager.flush();
        Optional<CartaoDeCredito> resultado = cartaoDeCreditoRepository.findById(cartaoId);
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve permitir apenas um cartão por ciclista")
    void devePermitirApenasUmCartaoPorCiclista() {
        cartaoDeCreditoRepository.save(cartaoDeCredito);
        entityManager.flush();
        Optional<CartaoDeCredito> resultado = cartaoDeCreditoRepository.findByCiclistaId(ciclista.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCiclista().getId()).isEqualTo(ciclista.getId());
    }
}

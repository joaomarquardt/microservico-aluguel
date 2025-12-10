package com.es2.microservicos.repositories;

import com.es2.microservicos.domain.Aluguel;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes de Integração - AluguelRepository")
class AluguelRepositoryIntegrationTest {

    @Autowired
    private AluguelRepository aluguelRepository;

    @Autowired
    private CiclistaRepository ciclistaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Ciclista ciclista;
    private Aluguel aluguel;

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

        aluguel = new Aluguel();
        aluguel.setCiclista(ciclista);
        aluguel.setHoraInicio(LocalDateTime.now().minusHours(1));
        aluguel.setCobranca(10.0);
        aluguel.setTrancaInicio(1L);
        aluguel.setBicicletaId(1L);
    }

    @Test
    @DisplayName("Deve salvar aluguel no banco de dados")
    void deveSalvarAluguelNoBancoDeDados() {
        Aluguel aluguelSalvo = aluguelRepository.save(aluguel);
        assertThat(aluguelSalvo.getId()).isNotNull();
        assertThat(aluguelSalvo.getCiclista().getId()).isEqualTo(ciclista.getId());
        assertThat(aluguelSalvo.getBicicletaId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve buscar aluguel por bicicleta ID e tranca fim null")
    void deveBuscarAluguelPorBicicletaIdETrancaFimNull() {
        aluguelRepository.save(aluguel);
        entityManager.flush();
        Optional<Aluguel> resultado = aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(1L);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getBicicletaId()).isEqualTo(1L);
        assertThat(resultado.get().getTrancaFim()).isNull();
    }

    @Test
    @DisplayName("Deve retornar vazio quando não há aluguel em andamento")
    void deveRetornarVazioQuandoNaoHaAluguelEmAndamento() {
        aluguel.setTrancaFim(2L);
        aluguel.setHoraFim(LocalDateTime.now());
        aluguelRepository.save(aluguel);
        entityManager.flush();
        Optional<Aluguel> resultado = aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(1L);
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar aluguel por ciclista ID")
    void deveBuscarAluguelPorCiclistaId() {
        aluguelRepository.save(aluguel);
        entityManager.flush();
        Optional<Aluguel> resultado = aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(ciclista.getId());
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCiclista().getId()).isEqualTo(ciclista.getId());
    }

    @Test
    @DisplayName("Deve verificar se existe aluguel em andamento para ciclista")
    void deveVerificarSeExisteAluguelEmAndamentoParaCiclista() {
        aluguelRepository.save(aluguel);
        entityManager.flush();
        Optional<Aluguel> aluguelOpt = aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(ciclista.getId());
        assertThat(aluguelOpt.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando não existe aluguel em andamento")
    void deveRetornarFalseQuandoNaoExisteAluguelEmAndamento() {
        Optional<Aluguel> aluguelOpt = aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(ciclista.getId());
        assertThat(aluguelOpt.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve manter relacionamento entre Aluguel e Ciclista")
    void deveManterRelacionamentoEntreAluguelECiclista() {
        Aluguel aluguelSalvo = aluguelRepository.save(aluguel);
        entityManager.flush();
        entityManager.clear();
        Aluguel aluguelBuscado = aluguelRepository.findById(aluguelSalvo.getId()).orElseThrow();
        assertThat(aluguelBuscado.getCiclista()).isNotNull();
        assertThat(aluguelBuscado.getCiclista().getNome()).isEqualTo("João Silva");
        assertThat(aluguelBuscado.getCiclista().getEmail()).isEqualTo("joao@test.com");
    }

    @Test
    @DisplayName("Deve atualizar hora fim e tranca fim do aluguel")
    void deveAtualizarHoraFimETrancaFimDoAluguel() {
        Aluguel aluguelSalvo = aluguelRepository.save(aluguel);
        entityManager.flush();
        aluguelSalvo.setHoraFim(LocalDateTime.now());
        aluguelSalvo.setTrancaFim(2L);
        aluguelRepository.save(aluguelSalvo);
        entityManager.flush();
        entityManager.clear();
        Aluguel aluguelAtualizado = aluguelRepository.findById(aluguelSalvo.getId()).orElseThrow();
        assertThat(aluguelAtualizado.getHoraFim()).isNotNull();
        assertThat(aluguelAtualizado.getTrancaFim()).isEqualTo(2L);
    }
}

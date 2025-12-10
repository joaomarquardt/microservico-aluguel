package com.es2.microservicos.services;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.GerarAluguelRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.dtos.responses.BicicletaResponse;
import com.es2.microservicos.dtos.responses.CobrancaResponse;
import com.es2.microservicos.external.domain.BicicletaStatus;
import com.es2.microservicos.external.domain.TrancaStatus;
import com.es2.microservicos.external.gateways.EquipamentoServiceGateway;
import com.es2.microservicos.external.gateways.ExternoServiceGateway;
import com.es2.microservicos.mappers.AluguelMapper;
import com.es2.microservicos.repositories.AluguelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AluguelService")
class AluguelServiceTest {

    @Mock
    private AluguelRepository aluguelRepository;

    @Mock
    private AluguelMapper aluguelMapper;

    @Mock
    private EquipamentoServiceGateway equipamentoServiceGateway;

    @Mock
    private ExternoServiceGateway externoServiceGateway;

    @Mock
    private CiclistaService ciclistaService;

    @InjectMocks
    private AluguelService aluguelService;

    private Ciclista ciclista;
    private BicicletaResponse bicicletaResponse;
    private GerarAluguelRequest gerarAluguelRequest;
    private Aluguel aluguel;
    private AluguelResponse aluguelResponse;

    @BeforeEach
    void setUp() {
        ciclista = new Ciclista();
        ciclista.setId(1L);
        ciclista.setNome("João Silva");
        ciclista.setEmail("joao@example.com");
        ciclista.setStatus(Status.ATIVO);

        bicicletaResponse = new BicicletaResponse(1L, "Bike001", "Caloi", "2023",  123, BicicletaStatus.DISPONIVEL);

        gerarAluguelRequest = new GerarAluguelRequest(1L, 1L);

        aluguel = new Aluguel(1L, ciclista, LocalDateTime.now(), null, 10.0, 1L, null);
        aluguel.setId(1L);

        aluguelResponse = new AluguelResponse(
                1L,
                1L,
                LocalDateTime.now(),
                1L,
                null,
                null,
                10.0,
                1L
        );
    }

    @Test
    @DisplayName("Deve gerar aluguel com sucesso quando dados válidos")
    void deveGerarAluguelComSucesso() {
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(1L)).thenReturn(bicicletaResponse);
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L)).thenReturn(Optional.empty());
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(externoServiceGateway.cobrarAluguel(anyDouble(), anyLong())).thenReturn(any(CobrancaResponse.class));
        AluguelResponse resultado = aluguelService.gerarAluguel(gerarAluguelRequest);
        assertThat(resultado).isNotNull();
        assertThat(resultado.bicicletaId()).isEqualTo(1L);
        verify(equipamentoServiceGateway).existeTrancaPorId(1L);
        verify(equipamentoServiceGateway).obterBicicletaPorIdTranca(1L);
        verify(ciclistaService).obterCiclistaPorId(1L);
        verify(externoServiceGateway).cobrarAluguel(10.0, 1L);
        verify(equipamentoServiceGateway).alterarStatusBicicleta(1L, BicicletaStatus.EM_USO);
        verify(equipamentoServiceGateway).alterarStatusTranca(1L, TrancaStatus.DESTRANCAR);
        verify(aluguelRepository).save(any(Aluguel.class));
        verify(externoServiceGateway).dadosAluguelNovoEmail(eq("joao@example.com"), any(AluguelResponse.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando tranca inicial não existe")
    void deveLancarExcecaoQuandoTrancaInicialNaoExiste() {
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(false);
        assertThatThrownBy(() -> aluguelService.gerarAluguel(gerarAluguelRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tranca inicial não existe!");
        verify(equipamentoServiceGateway).existeTrancaPorId(1L);
        verify(equipamentoServiceGateway, never()).obterBicicletaPorIdTranca(anyLong());
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ciclista está inativo")
    void deveLancarExcecaoQuandoCiclistaInativo() {
        ciclista.setStatus(Status.INATIVO);
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(1L)).thenReturn(bicicletaResponse);
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        assertThatThrownBy(() -> aluguelService.gerarAluguel(gerarAluguelRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ciclista com status INATIVO não pode alugar bicicleta!");
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção e enviar email quando ciclista já possui aluguel em andamento")
    void deveLancarExcecaoQuandoCiclistaJaPossuiAluguel() {
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(1L)).thenReturn(bicicletaResponse);
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L)).thenReturn(Optional.of(aluguel));
        when(aluguelMapper.toAluguelResponse(aluguel)).thenReturn(aluguelResponse);
        doNothing().when(externoServiceGateway).dadosAluguelAtualEmail(anyString(), any(AluguelResponse.class));
        assertThatThrownBy(() -> aluguelService.gerarAluguel(gerarAluguelRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O ciclista já possui um aluguel em andamento e não pode alugar outra bicicleta!");
        verify(externoServiceGateway).dadosAluguelAtualEmail(eq("joao@example.com"), any(AluguelResponse.class));
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando bicicleta está em reparo")
    void deveLancarExcecaoQuandoBicicletaEmReparo() {
        BicicletaResponse bicicletaEmReparo = new BicicletaResponse(
                1L, "Bike001", "Caloi", "2023", 123, BicicletaStatus.EM_REPARO
        );
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(1L)).thenReturn(bicicletaEmReparo);
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> aluguelService.gerarAluguel(gerarAluguelRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A bicicleta solicitada para uso está EM REPARO e não pode ser utilizada!");
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando bicicleta está com reparo solicitado")
    void deveLancarExcecaoQuandoBicicletaComReparoSolicitado() {
        BicicletaResponse bicicletaReparoSolicitado = new BicicletaResponse(
                1L, "Bike001", "Caloi",  "2023", 123, BicicletaStatus.REPARO_SOLICITADO
        );
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(1L)).thenReturn(bicicletaReparoSolicitado);
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> aluguelService.gerarAluguel(gerarAluguelRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A bicicleta solicitada para uso está EM REPARO e não pode ser utilizada!");
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve verificar permissão de aluguel quando ciclista não tem aluguel em andamento")
    void deveVerificarPermissaoAluguelQuandoNaoTemAluguelEmAndamento() {
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L)).thenReturn(Optional.empty());
        Boolean resultado = aluguelService.verificarPermissaoAluguel(1L);
        assertThat(resultado).isTrue();
        verify(aluguelRepository).findByCiclistaIdAndTrancaFimIsNull(1L);
    }

    @Test
    @DisplayName("Deve negar permissão de aluguel quando ciclista tem aluguel em andamento")
    void deveNegarPermissaoAluguelQuandoTemAluguelEmAndamento() {
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L)).thenReturn(Optional.of(aluguel));
        Boolean resultado = aluguelService.verificarPermissaoAluguel(1L);
        assertThat(resultado).isFalse();
        verify(aluguelRepository).findByCiclistaIdAndTrancaFimIsNull(1L);
    }

    @Test
    @DisplayName("Deve obter bicicleta alugada por ID do ciclista")
    void deveObterBicicletaAlugadaPorIdCiclista() {
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L)).thenReturn(Optional.of(aluguel));
        when(equipamentoServiceGateway.obterBicicletaPorId(1L)).thenReturn(bicicletaResponse);
        BicicletaResponse resultado = aluguelService.obterBicicletaAlugadaPorIdCiclista(1L);
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        verify(ciclistaService).obterCiclistaPorId(1L);
        verify(aluguelRepository).findByCiclistaIdAndTrancaFimIsNull(1L);
        verify(equipamentoServiceGateway).obterBicicletaPorId(1L);
    }

    @Test
    @DisplayName("Deve retornar null quando ciclista não tem bicicleta alugada")
    void deveRetornarNullQuandoCiclistaNaoTemBicicletaAlugada() {
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L)).thenReturn(Optional.empty());
        BicicletaResponse resultado = aluguelService.obterBicicletaAlugadaPorIdCiclista(1L);
        assertThat(resultado).isNull();
        verify(ciclistaService).obterCiclistaPorId(1L);
        verify(aluguelRepository).findByCiclistaIdAndTrancaFimIsNull(1L);
        verify(equipamentoServiceGateway, never()).obterBicicletaPorId(anyLong());
    }
}

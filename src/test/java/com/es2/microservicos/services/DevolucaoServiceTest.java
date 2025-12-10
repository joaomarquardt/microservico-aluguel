package com.es2.microservicos.services;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.DevolucaoRequest;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DevolucaoService")
class DevolucaoServiceTest {

    @Mock
    private AluguelRepository aluguelRepository;

    @Mock
    private AluguelMapper aluguelMapper;

    @Mock
    private EquipamentoServiceGateway equipamentoServiceGateway;

    @Mock
    private ExternoServiceGateway externoServiceGateway;

    @InjectMocks
    private DevolucaoService devolucaoService;

    private Ciclista ciclista;
    private Aluguel aluguel;
    private BicicletaResponse bicicletaResponse;
    private DevolucaoRequest devolucaoRequest;
    private AluguelResponse aluguelResponse;
    private CobrancaResponse cobrancaResponse;

    @BeforeEach
    void setUp() {
        ciclista = new Ciclista();
        ciclista.setId(1L);
        ciclista.setNome("João Silva");
        ciclista.setEmail("joao@example.com");
        ciclista.setStatus(Status.ATIVO);

        LocalDateTime horaInicio = LocalDateTime.now().minusHours(1);
        aluguel = new Aluguel(1L, ciclista, horaInicio, null, 10.0, 1L, null);
        aluguel.setId(1L);

        bicicletaResponse = new BicicletaResponse(2L, "Bike001", "Caloi", "2023", 123, BicicletaStatus.EM_USO);

        devolucaoRequest = new DevolucaoRequest(1L, 2L, false);

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
        cobrancaResponse = new CobrancaResponse(1L, "Cobrança processada", Instant.now().toString(), null, 5.0, ciclista.getId());
    }

    @Test
    @DisplayName("Deve devolver bicicleta com sucesso sem cobrança extra (menos de 2 horas)")
    void deveDevolverBicicletaSemCobrancaExtra() {
        when(equipamentoServiceGateway.obterBicicletaPorId(2L)).thenReturn(bicicletaResponse);
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(2L)).thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(equipamentoServiceGateway.alterarStatusBicicleta(anyLong(), any(BicicletaStatus.class)))
                .thenReturn(bicicletaResponse);
        doNothing().when(externoServiceGateway).devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble());
        AluguelResponse resultado = devolucaoService.devolverBicicleta(devolucaoRequest);
        assertThat(resultado).isNotNull();
        verify(equipamentoServiceGateway).obterBicicletaPorId(2L);
        verify(equipamentoServiceGateway).existeTrancaPorId(1L);
        verify(aluguelRepository).findByBicicletaIdAndTrancaFimIsNull(2L);
        verify(equipamentoServiceGateway).alterarStatusBicicleta(2L, BicicletaStatus.DISPONIVEL);
        verify(equipamentoServiceGateway).alterarStatusTranca(1L, TrancaStatus.TRANCAR);
        verify(externoServiceGateway, never()).cobrarAluguel(anyDouble(), anyLong());
        verify(externoServiceGateway).devolucaoBicicletaEmail(
                eq("João Silva"),
                eq("joao@example.com"),
                any(AluguelResponse.class),
                eq(0.0)
        );
    }

    @Test
    @DisplayName("Deve devolver bicicleta e cobrar taxa extra (mais de 2 horas)")
    void deveDevolverBicicletaComCobrancaExtra() {
        LocalDateTime horaInicio = LocalDateTime.now().minusHours(3);
        aluguel.setHoraInicio(horaInicio);
        when(equipamentoServiceGateway.obterBicicletaPorId(2L)).thenReturn(bicicletaResponse);
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(2L)).thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(equipamentoServiceGateway.alterarStatusBicicleta(anyLong(), any(BicicletaStatus.class)))
                .thenReturn(bicicletaResponse);
        when(externoServiceGateway.cobrarAluguel(anyDouble(), anyLong())).thenReturn(cobrancaResponse);
        doNothing().when(externoServiceGateway).devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble());
        AluguelResponse resultado = devolucaoService.devolverBicicleta(devolucaoRequest);
        assertThat(resultado).isNotNull();
        verify(externoServiceGateway).cobrarAluguel(anyDouble(), eq(1L));
        ArgumentCaptor<Double> valorCaptor = ArgumentCaptor.forClass(Double.class);
        verify(externoServiceGateway).devolucaoBicicletaEmail(
                eq("João Silva"),
                eq("joao@example.com"),
                any(AluguelResponse.class),
                valorCaptor.capture()
        );
        assertThat(valorCaptor.getValue()).isGreaterThan(0.0);
    }

    @Test
    @DisplayName("Deve solicitar reparo ao devolver bicicleta")
    void deveSolicitarReparoAoDevolver() {
        DevolucaoRequest requestComReparo = new DevolucaoRequest(1L, 2L, true);
        when(equipamentoServiceGateway.obterBicicletaPorId(2L)).thenReturn(bicicletaResponse);
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(2L)).thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(equipamentoServiceGateway.alterarStatusBicicleta(anyLong(), any(BicicletaStatus.class)))
                .thenReturn(bicicletaResponse);
        doNothing().when(externoServiceGateway).devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble());
        AluguelResponse resultado = devolucaoService.devolverBicicleta(requestComReparo);
        assertThat(resultado).isNotNull();
        verify(equipamentoServiceGateway).alterarStatusBicicleta(2L, BicicletaStatus.DISPONIVEL);
        verify(equipamentoServiceGateway).alterarStatusBicicleta(2L, BicicletaStatus.REPARO_SOLICITADO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando bicicleta não está em uso")
    void deveLancarExcecaoQuandoBicicletaNaoEstaEmUso() {
        BicicletaResponse bicicletaDisponivel = new BicicletaResponse(
                2L, "Bike001", "Caloi", "2023", 123, BicicletaStatus.DISPONIVEL
        );
        when(equipamentoServiceGateway.obterBicicletaPorId(2L)).thenReturn(bicicletaDisponivel);
        assertThatThrownBy(() -> devolucaoService.devolverBicicleta(devolucaoRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bicicleta não está em uso!");
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve redirecionar para totem quando bicicleta está nova ou em reparo")
    void deveRedirecionarParaTotemQuandoBicicletaNovaOuEmReparo() {
        BicicletaResponse bicicletaNova = new BicicletaResponse(
                2L, "Bike001", "Caloi", "2023", 123, BicicletaStatus.NOVA
        );
        when(equipamentoServiceGateway.obterBicicletaPorId(2L)).thenReturn(bicicletaNova);
        when(equipamentoServiceGateway.alterarStatusBicicleta(2L, BicicletaStatus.DISPONIVEL))
                .thenReturn(bicicletaNova);
        assertThatThrownBy(() -> devolucaoService.devolverBicicleta(devolucaoRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Redirecionando para integração com totem");
        verify(equipamentoServiceGateway).alterarStatusBicicleta(2L, BicicletaStatus.DISPONIVEL);
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando tranca não existe")
    void deveLancarExcecaoQuandoTrancaNaoExiste() {
        when(equipamentoServiceGateway.obterBicicletaPorId(2L)).thenReturn(bicicletaResponse);
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(false);
        assertThatThrownBy(() -> devolucaoService.devolverBicicleta(devolucaoRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tranca com ID 1 não existe!");
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando não existe aluguel em andamento")
    void deveLancarExcecaoQuandoNaoExisteAluguelEmAndamento() {
        when(equipamentoServiceGateway.obterBicicletaPorId(2L)).thenReturn(bicicletaResponse);
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> devolucaoService.devolverBicicleta(devolucaoRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Não existe aluguel em andamento para esta bicicleta!");
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve calcular valor extra corretamente para 2h30min de uso")
    void deveCalcularValorExtraCorretamente() {
        LocalDateTime horaInicio = LocalDateTime.now().minusMinutes(150);
        aluguel.setHoraInicio(horaInicio);
        when(equipamentoServiceGateway.obterBicicletaPorId(2L)).thenReturn(bicicletaResponse);
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(2L)).thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(equipamentoServiceGateway.alterarStatusBicicleta(anyLong(), any(BicicletaStatus.class)))
                .thenReturn(bicicletaResponse);
        when(externoServiceGateway.cobrarAluguel(anyDouble(), anyLong())).thenReturn(cobrancaResponse);
        doNothing().when(externoServiceGateway).devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble());
        devolucaoService.devolverBicicleta(devolucaoRequest);
        verify(externoServiceGateway).cobrarAluguel(5.0, 1L);
        verify(externoServiceGateway).devolucaoBicicletaEmail(
                anyString(),
                anyString(),
                any(AluguelResponse.class),
                eq(5.0)
        );
    }

    @Test
    @DisplayName("Deve alterar status da tranca para TRANCAR após devolução")
    void deveAlterarStatusTrancaParaTrancarAposDevolucao() {
        when(equipamentoServiceGateway.obterBicicletaPorId(2L)).thenReturn(bicicletaResponse);
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(2L)).thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(equipamentoServiceGateway.alterarStatusBicicleta(anyLong(), any(BicicletaStatus.class)))
                .thenReturn(bicicletaResponse);
        doNothing().when(externoServiceGateway).devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble());
        devolucaoService.devolverBicicleta(devolucaoRequest);
        verify(equipamentoServiceGateway).alterarStatusTranca(1L, TrancaStatus.TRANCAR);
    }

    @Test
    @DisplayName("Deve alterar status da bicicleta para DISPONIVEL após devolução")
    void deveAlterarStatusBicicletaParaDisponivelAposDevolucao() {
        when(equipamentoServiceGateway.obterBicicletaPorId(2L)).thenReturn(bicicletaResponse);
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(2L)).thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(equipamentoServiceGateway.alterarStatusBicicleta(anyLong(), any(BicicletaStatus.class)))
                .thenReturn(bicicletaResponse);
        doNothing().when(externoServiceGateway).devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble());
        devolucaoService.devolverBicicleta(devolucaoRequest);
        verify(equipamentoServiceGateway).alterarStatusBicicleta(2L, BicicletaStatus.DISPONIVEL);
    }

    @Test
    @DisplayName("Deve persistir hora fim e tranca fim no aluguel")
    void devePersistirHoraFimETrancaFimNoAluguel() {
        when(equipamentoServiceGateway.obterBicicletaPorId(2L)).thenReturn(bicicletaResponse);
        when(equipamentoServiceGateway.existeTrancaPorId(1L)).thenReturn(true);
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(2L)).thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(equipamentoServiceGateway.alterarStatusBicicleta(anyLong(), any(BicicletaStatus.class)))
                .thenReturn(bicicletaResponse);
        doNothing().when(externoServiceGateway).devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble());
        devolucaoService.devolverBicicleta(devolucaoRequest);
        ArgumentCaptor<Aluguel> aluguelCaptor = ArgumentCaptor.forClass(Aluguel.class);
        verify(aluguelRepository).save(aluguelCaptor.capture());
        Aluguel aluguelSalvo = aluguelCaptor.getValue();
        assertThat(aluguelSalvo.getHoraFim()).isNotNull();
        assertThat(aluguelSalvo.getTrancaFim()).isEqualTo(1L);
    }
}

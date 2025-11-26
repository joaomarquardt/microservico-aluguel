package com.es2.microservicos.services;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Nacionalidade;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.GerarAluguelRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.dtos.responses.BicicletaResponse;
import com.es2.microservicos.dtos.responses.CartaoResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para AluguelService")
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

    @Mock
    private CartaoDeCreditoService cartaoDeCreditoService;

    @InjectMocks
    private AluguelService aluguelService;

    private GerarAluguelRequest gerarAluguelRequest;
    private Ciclista ciclista;
    private BicicletaResponse bicicletaResponse;
    private CartaoResponse cartaoResponse;
    private Aluguel aluguel;
    private AluguelResponse aluguelResponse;

    @BeforeEach
    void setUp() {
        gerarAluguelRequest = new GerarAluguelRequest(1L, 100L);

        ciclista = new Ciclista();
        ciclista.setId(1L);
        ciclista.setNome("João Silva");
        ciclista.setEmail("joao@email.com");
        ciclista.setStatus(Status.ATIVO);
        ciclista.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclista.setCpf("12345678901");

        bicicletaResponse = new BicicletaResponse(
                50L,
                "Caloi",
                "Mountain Bike",
                "2023",
                123,
                BicicletaStatus.DISPONIVEL
        );

        cartaoResponse = new CartaoResponse(
                "João Silva",
                "1234567890123456",
                LocalDate.of(2027, 12, 31),
                "123"
        );

        aluguel = new Aluguel();
        aluguel.setId(1L);
        aluguel.setBicicletaId(50L);
        aluguel.setCiclista(ciclista);
        aluguel.setHoraInicio(LocalDateTime.now());
        aluguel.setCobranca(10.0);
        aluguel.setTrancaInicio(100L);

        aluguelResponse = new AluguelResponse(
                1L,
                LocalDateTime.now(),
                1L,
                null,
                null,
                10,
                100L
        );
    }

    @Test
    @DisplayName("Deve gerar aluguel com sucesso")
    void deveGerarAluguelComSucesso() {
        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(100L))
                .thenReturn(ResponseEntity.ok(bicicletaResponse));
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L))
                .thenReturn(Optional.empty());
        when(cartaoDeCreditoService.obterCartaoDeCreditoPorIdCiclista(1L))
                .thenReturn(cartaoResponse);
        when(externoServiceGateway.cobrarAluguel(anyDouble(), any(CartaoResponse.class)))
                .thenReturn(ResponseEntity.ok().build());
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(equipamentoServiceGateway.alterarStatusBicicleta(anyLong(), any(BicicletaStatus.class)))
                .thenReturn(ResponseEntity.ok().build());
        when(equipamentoServiceGateway.alterarStatusTranca(anyLong(), any(TrancaStatus.class)))
                .thenReturn(ResponseEntity.ok().build());
        when(externoServiceGateway.dadosAluguelNovoEmail(any(AluguelResponse.class)))
                .thenReturn(ResponseEntity.ok().build());

        AluguelResponse resultado = aluguelService.gerarAluguel(gerarAluguelRequest);

        assertNotNull(resultado);
        assertEquals(1L, resultado.bicicletaId());
        verify(aluguelRepository, times(1)).save(any(Aluguel.class));
        verify(equipamentoServiceGateway, times(1)).alterarStatusBicicleta(50L, BicicletaStatus.EM_USO);
        verify(equipamentoServiceGateway, times(1)).alterarStatusTranca(100L, TrancaStatus.DESTRANCAR);
        verify(externoServiceGateway, times(1)).dadosAluguelNovoEmail(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tranca não existe")
    void deveLancarExcecaoQuandoTrancaNaoExiste() {
        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(false));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aluguelService.gerarAluguel(gerarAluguelRequest)
        );

        assertEquals("Tranca inicial não existe!", exception.getMessage());
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando não existe bicicleta na tranca")
    void deveLancarExcecaoQuandoNaoExisteBicicletaNaTranca() {
        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(100L))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aluguelService.gerarAluguel(gerarAluguelRequest)
        );

        assertEquals("Não existe bicicleta na tranca com ID: 100", exception.getMessage());
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ciclista está inativo")
    void deveLancarExcecaoQuandoCiclistaEstaInativo() {
        ciclista.setStatus(Status.INATIVO);

        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(100L))
                .thenReturn(ResponseEntity.ok(bicicletaResponse));
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aluguelService.gerarAluguel(gerarAluguelRequest)
        );

        assertEquals("Ciclista com status INATIVO não pode alugar bicicleta!", exception.getMessage());
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ciclista já possui aluguel em andamento")
    void deveLancarExcecaoQuandoCiclistaJaPossuiAluguelEmAndamento() {
        Aluguel aluguelEmAndamento = new Aluguel();
        aluguelEmAndamento.setId(2L);
        aluguelEmAndamento.setCiclista(ciclista);
        aluguelEmAndamento.setTrancaFim(null);

        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(100L))
                .thenReturn(ResponseEntity.ok(bicicletaResponse));
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L))
                .thenReturn(Optional.of(aluguelEmAndamento));
        when(aluguelMapper.toAluguelResponse(aluguelEmAndamento)).thenReturn(aluguelResponse);
        when(externoServiceGateway.dadosAluguelAtualEmail(any()))
                .thenReturn(ResponseEntity.ok().build());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aluguelService.gerarAluguel(gerarAluguelRequest)
        );

        assertEquals("O ciclista já possui um aluguel em andamento e não pode alugar outra bicicleta!", exception.getMessage());
        verify(externoServiceGateway, times(1)).dadosAluguelAtualEmail(any());
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando bicicleta está em reparo")
    void deveLancarExcecaoQuandoBicicletaEstaEmReparo() {
        BicicletaResponse bicicletaEmReparo = new BicicletaResponse(
                50L, "Caloi", "Mountain Bike", "2023", 123, BicicletaStatus.EM_REPARO
        );

        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(100L))
                .thenReturn(ResponseEntity.ok(bicicletaEmReparo));
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aluguelService.gerarAluguel(gerarAluguelRequest)
        );

        assertEquals("A bicicleta solicitada para uso está EM REPARO e não pode ser utilizada!", exception.getMessage());
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando bicicleta tem reparo solicitado")
    void deveLancarExcecaoQuandoBicicletaTemReparoSolicitado() {
        BicicletaResponse bicicletaReparoSolicitado = new BicicletaResponse(
                50L, "Caloi", "Mountain Bike", "2023", 123, BicicletaStatus.REPARO_SOLICITADO
        );

        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(100L))
                .thenReturn(ResponseEntity.ok(bicicletaReparoSolicitado));
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aluguelService.gerarAluguel(gerarAluguelRequest)
        );

        assertEquals("A bicicleta solicitada para uso está EM REPARO e não pode ser utilizada!", exception.getMessage());
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento falha")
    void deveLancarExcecaoQuandoPagamentoFalha() {
        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(100L))
                .thenReturn(ResponseEntity.ok(bicicletaResponse));
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.findByCiclistaIdAndTrancaFimIsNull(1L))
                .thenReturn(Optional.empty());
        when(cartaoDeCreditoService.obterCartaoDeCreditoPorIdCiclista(1L))
                .thenReturn(cartaoResponse);
        when(externoServiceGateway.cobrarAluguel(anyDouble(), any(CartaoResponse.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aluguelService.gerarAluguel(gerarAluguelRequest)
        );

        assertEquals("O pagamento não foi concluído devido a erro no pagamento ou pagamento não autorizado", exception.getMessage());
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve retornar null ao obter bicicleta alugada (TODO)")
    void deveRetornarNullAoObterBicicletaAlugada() {
        var resultado = aluguelService.obterBicicletaAlugadaPorIdCiclista(1L);
        assertNull(resultado);
        verify(equipamentoServiceGateway, times(1)).obterBicicletaPorId(1L);
    }
}

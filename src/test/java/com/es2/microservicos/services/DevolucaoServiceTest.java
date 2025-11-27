package com.es2.microservicos.services;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Nacionalidade;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.DevolucaoRequest;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para DevolucaoService")
class DevolucaoServiceTest {

    @Mock
    private AluguelRepository aluguelRepository;

    @Mock
    private AluguelMapper aluguelMapper;

    @Mock
    private EquipamentoServiceGateway equipamentoServiceGateway;

    @Mock
    private ExternoServiceGateway externoServiceGateway;

    @Mock
    private CartaoDeCreditoService cartaoDeCreditoService;

    @InjectMocks
    private DevolucaoService devolucaoService;

    private DevolucaoRequest devolucaoRequest;
    private Ciclista ciclista;
    private Aluguel aluguel;
    private BicicletaResponse bicicletaResponse;
    private CartaoResponse cartaoResponse;
    private AluguelResponse aluguelResponse;

    @BeforeEach
    void setUp() {
        devolucaoRequest = new DevolucaoRequest(100L, 50L, false);

        ciclista = new Ciclista();
        ciclista.setId(1L);
        ciclista.setNome("João Silva");
        ciclista.setEmail("joao@email.com");
        ciclista.setStatus(Status.ATIVO);
        ciclista.setNacionalidade(Nacionalidade.BRASILEIRO);

        aluguel = new Aluguel();
        aluguel.setId(1L);
        aluguel.setBicicletaId(50L);
        aluguel.setCiclista(ciclista);
        aluguel.setHoraInicio(LocalDateTime.now().minusHours(1));
        aluguel.setCobranca(10.0);
        aluguel.setTrancaInicio(200L);

        bicicletaResponse = new BicicletaResponse(
                50L, "Caloi", "Elite", "2023", 100, BicicletaStatus.EM_USO
        );

        cartaoResponse = new CartaoResponse(
                1L, "João Silva", "1234567890123456", LocalDate.of(2027, 12, 31), "123"
        );

        aluguelResponse = new AluguelResponse(
                1L, 50L, aluguel.getHoraInicio(), 1L, null, null, 10.0, 200L
        );
    }

    @Test
    @DisplayName("Deve devolver bicicleta com sucesso sem taxa extra")
    void deveDevolverBicicletaComSucessoSemTaxaExtra() {
        when(equipamentoServiceGateway.obterBicicletaPorId(50L))
                .thenReturn(ResponseEntity.ok(bicicletaResponse));
        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(50L))
                .thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(externoServiceGateway.devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble()))
                .thenReturn(ResponseEntity.ok().build());
        AluguelResponse resultado = devolucaoService.devolverBicicleta(devolucaoRequest);
        assertNotNull(resultado);
        verify(aluguelRepository, times(1)).save(any(Aluguel.class));
        verify(equipamentoServiceGateway, times(1)).alterarStatusBicicleta(50L, BicicletaStatus.DISPONIVEL);
        verify(equipamentoServiceGateway, times(1)).alterarStatusTranca(100L, TrancaStatus.TRANCAR);
        verify(externoServiceGateway, times(1)).devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble());
    }

    @Test
    @DisplayName("Deve cobrar taxa extra quando aluguel excede 2 horas")
    void deveCobrarrTaxaExtraQuandoExcede2Horas() {
        aluguel.setHoraInicio(LocalDateTime.now().minusHours(3));
        when(equipamentoServiceGateway.obterBicicletaPorId(50L))
                .thenReturn(ResponseEntity.ok(bicicletaResponse));
        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(50L))
                .thenReturn(Optional.of(aluguel));
        when(cartaoDeCreditoService.obterCartaoDeCreditoPorIdCiclista(1L))
                .thenReturn(cartaoResponse);
        when(externoServiceGateway.cobrarAluguel(anyDouble(), any(CartaoResponse.class)))
                .thenReturn(ResponseEntity.ok().build());
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(externoServiceGateway.devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble()))
                .thenReturn(ResponseEntity.ok().build());
        AluguelResponse resultado = devolucaoService.devolverBicicleta(devolucaoRequest);
        assertNotNull(resultado);
        verify(cartaoDeCreditoService, times(1)).obterCartaoDeCreditoPorIdCiclista(1L);
        verify(externoServiceGateway, times(1)).cobrarAluguel(anyDouble(), any(CartaoResponse.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando bicicleta não existe")
    void deveLancarExcecaoQuandoBicicletaNaoExiste() {
        when(equipamentoServiceGateway.obterBicicletaPorId(50L))
                .thenReturn(ResponseEntity.ok().build());
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> devolucaoService.devolverBicicleta(devolucaoRequest)
        );
        assertTrue(exception.getMessage().contains("Não existe bicicleta"));
        verify(aluguelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tranca não existe")
    void deveLancarExcecaoQuandoTrancaNaoExiste() {
        when(equipamentoServiceGateway.obterBicicletaPorId(50L))
                .thenReturn(ResponseEntity.ok(bicicletaResponse));
        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(false));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> devolucaoService.devolverBicicleta(devolucaoRequest)
        );
        assertTrue(exception.getMessage().contains("Não existe tranca"));
        verify(aluguelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando bicicleta não está em uso")
    void deveLancarExcecaoQuandoBicicletaNaoEstaEmUso() {
        BicicletaResponse bicicletaDisponivel = new BicicletaResponse(
                50L, "Caloi", "Elite", "2023", 100, BicicletaStatus.DISPONIVEL
        );
        when(equipamentoServiceGateway.obterBicicletaPorId(50L))
                .thenReturn(ResponseEntity.ok(bicicletaDisponivel));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> devolucaoService.devolverBicicleta(devolucaoRequest)
        );
        assertEquals("Bicicleta não está em uso!", exception.getMessage());
        verify(aluguelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve alterar status para disponível quando bicicleta nova ou em reparo")
    void deveAlterarStatusParaDisponivelQuandoBicicletaNovaOuEmReparo() {
        BicicletaResponse bicicletaNova = new BicicletaResponse(
                50L, "Caloi", "Elite", "2023", 100, BicicletaStatus.NOVA
        );
        when(equipamentoServiceGateway.obterBicicletaPorId(50L))
                .thenReturn(ResponseEntity.ok(bicicletaNova));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> devolucaoService.devolverBicicleta(devolucaoRequest)
        );
        assertTrue(exception.getMessage().contains("Redirecionando para integração com totem"));
        verify(equipamentoServiceGateway, times(1)).alterarStatusBicicleta(50L, BicicletaStatus.DISPONIVEL);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não existe aluguel em andamento")
    void deveLancarExcecaoQuandoNaoExisteAluguelEmAndamento() {
        when(equipamentoServiceGateway.obterBicicletaPorId(50L))
                .thenReturn(ResponseEntity.ok(bicicletaResponse));
        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(50L))
                .thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> devolucaoService.devolverBicicleta(devolucaoRequest)
        );
        assertEquals("Não existe aluguel em andamento para esta bicicleta!", exception.getMessage());
        verify(aluguelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve alterar status para reparo solicitado quando requisitado")
    void deveAlterarStatusParaReparoSolicitadoQuandoRequisitado() {
        DevolucaoRequest requestComReparo = new DevolucaoRequest(100L, 50L, true);
        when(equipamentoServiceGateway.obterBicicletaPorId(50L))
                .thenReturn(ResponseEntity.ok(bicicletaResponse));
        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(50L))
                .thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(externoServiceGateway.devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble()))
                .thenReturn(ResponseEntity.ok().build());
        AluguelResponse resultado = devolucaoService.devolverBicicleta(requestComReparo);
        assertNotNull(resultado);
        verify(equipamentoServiceGateway, times(1)).alterarStatusBicicleta(50L, BicicletaStatus.DISPONIVEL);
        verify(equipamentoServiceGateway, times(1)).alterarStatusBicicleta(50L, BicicletaStatus.REPARO_SOLICITADO);
    }

    @Test
    @DisplayName("Deve calcular taxa extra corretamente para 2h30min")
    void deveCalcularTaxaExtraCorretamenteParaDuasHorasMeia() {
        aluguel.setHoraInicio(LocalDateTime.now().minusMinutes(150));
        when(equipamentoServiceGateway.obterBicicletaPorId(50L))
                .thenReturn(ResponseEntity.ok(bicicletaResponse));
        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(50L))
                .thenReturn(Optional.of(aluguel));
        when(cartaoDeCreditoService.obterCartaoDeCreditoPorIdCiclista(1L))
                .thenReturn(cartaoResponse);
        when(externoServiceGateway.cobrarAluguel(eq(5.0), any(CartaoResponse.class)))
                .thenReturn(ResponseEntity.ok().build());
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(externoServiceGateway.devolucaoBicicletaEmail(anyString(), anyString(), any(), eq(5.0)))
                .thenReturn(ResponseEntity.ok().build());
        devolucaoService.devolverBicicleta(devolucaoRequest);
        verify(externoServiceGateway, times(1)).cobrarAluguel(eq(5.0), any(CartaoResponse.class));
    }

    @Test
    @DisplayName("Deve registrar cobrança mesmo quando pagamento falha")
    void deveRegistrarCobrancaMesmoQuandoPagamentoFalha() {
        aluguel.setHoraInicio(LocalDateTime.now().minusHours(3));
        when(equipamentoServiceGateway.obterBicicletaPorId(50L))
                .thenReturn(ResponseEntity.ok(bicicletaResponse));
        when(equipamentoServiceGateway.existeTrancaPorId(100L))
                .thenReturn(ResponseEntity.ok(true));
        when(aluguelRepository.findByBicicletaIdAndTrancaFimIsNull(50L))
                .thenReturn(Optional.of(aluguel));
        when(cartaoDeCreditoService.obterCartaoDeCreditoPorIdCiclista(1L))
                .thenReturn(cartaoResponse);
        when(externoServiceGateway.cobrarAluguel(anyDouble(), any(CartaoResponse.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);
        when(externoServiceGateway.devolucaoBicicletaEmail(anyString(), anyString(), any(), anyDouble()))
                .thenReturn(ResponseEntity.ok().build());
        AluguelResponse resultado = devolucaoService.devolverBicicleta(devolucaoRequest);
        assertNotNull(resultado);
        verify(aluguelRepository, times(1)).save(any(Aluguel.class));
    }
}

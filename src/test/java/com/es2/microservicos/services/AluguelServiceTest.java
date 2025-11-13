package com.es2.microservicos.services;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.GerarAluguelRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.dtos.responses.BicicletaResponse;
import com.es2.microservicos.gateways.EquipamentoServiceGateway;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private CiclistaService ciclistaService;

    @InjectMocks
    private AluguelService aluguelService;

    private GerarAluguelRequest gerarAluguelRequest;
    private Ciclista ciclista;
    private BicicletaResponse bicicletaResponse;
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

        bicicletaResponse = new BicicletaResponse(
                50L,
                "Marca X",
                "Modelo Y",
                "2023",
                123,
                "DISPONIVEL"
        );

        aluguel = new Aluguel();
        aluguel.setId(1L);
        aluguel.setBicicletaId(50L);
        aluguel.setCiclista(ciclista);
        aluguel.setHoraInicio(LocalDateTime.now());
        aluguel.setTrancaInicio(100L);

        aluguelResponse = new AluguelResponse(
                1L,
                LocalDateTime.now(),
                1L,
                LocalDateTime.now().plusMinutes(5),
                null,
                null,
                100L
        );
    }

    @Test
    @DisplayName("Deve gerar aluguel com sucesso")
    void deveGerarAluguelComSucesso() {
        when(equipamentoServiceGateway.existeTrancaPorId(100L)).thenReturn(true);
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(100L)).thenReturn(bicicletaResponse);
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);

        AluguelResponse resultado = aluguelService.gerarAluguel(gerarAluguelRequest);

        assertNotNull(resultado);
        assertEquals(1L, resultado.bicicletaId());
        verify(equipamentoServiceGateway, times(1)).existeTrancaPorId(100L);
        verify(equipamentoServiceGateway, times(1)).obterBicicletaPorIdTranca(100L);
        verify(ciclistaService, times(1)).obterCiclistaPorId(1L);
        verify(aluguelRepository, times(1)).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando tranca não existe")
    void deveLancarExcecaoQuandoTrancaNaoExiste() {
        when(equipamentoServiceGateway.existeTrancaPorId(100L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aluguelService.gerarAluguel(gerarAluguelRequest)
        );

        assertEquals("Tranca inicial não existe!", exception.getMessage());
        verify(equipamentoServiceGateway, times(1)).existeTrancaPorId(100L);
        verify(equipamentoServiceGateway, never()).obterBicicletaPorIdTranca(anyLong());
        verify(aluguelRepository, never()).save(any(Aluguel.class));
    }

    @Test
    @DisplayName("Deve gerar aluguel com tranca válida")
    void deveGerarAluguelComTrancaValida() {
        when(equipamentoServiceGateway.existeTrancaPorId(anyLong())).thenReturn(true);
        when(equipamentoServiceGateway.obterBicicletaPorIdTranca(anyLong())).thenReturn(bicicletaResponse);
        when(ciclistaService.obterCiclistaPorId(anyLong())).thenReturn(ciclista);
        when(aluguelRepository.save(any(Aluguel.class))).thenReturn(aluguel);
        when(aluguelMapper.toAluguelResponse(any(Aluguel.class))).thenReturn(aluguelResponse);

        AluguelResponse resultado = aluguelService.gerarAluguel(gerarAluguelRequest);

        assertNotNull(resultado);
        verify(equipamentoServiceGateway, times(1)).existeTrancaPorId(100L);
    }
}

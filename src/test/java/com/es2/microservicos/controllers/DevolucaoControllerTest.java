package com.es2.microservicos.controllers;

import com.es2.microservicos.dtos.requests.DevolucaoRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.services.DevolucaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para DevolucaoController")
class DevolucaoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private DevolucaoService devolucaoService;

    @InjectMocks
    private DevolucaoController devolucaoController;

    private DevolucaoRequest devolucaoRequest;
    private AluguelResponse aluguelResponse;

    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(devolucaoController)
                .setControllerAdvice(new TestExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        devolucaoRequest = new DevolucaoRequest(100L, 50L, false);

        aluguelResponse = new AluguelResponse(
                1L, 50L, LocalDateTime.now().minusHours(1), 1L, LocalDateTime.now(), 2L, 10.0, 200L
        );
    }

    @Test
    @DisplayName("Deve devolver bicicleta e retornar 200 OK")
    void deveDevolverBicicletaERetornar200() throws Exception {
        when(devolucaoService.devolverBicicleta(any(DevolucaoRequest.class)))
                .thenReturn(aluguelResponse);

        mockMvc.perform(post("/devolucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devolucaoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bicicletaId").value(50));

        verify(devolucaoService, times(1)).devolverBicicleta(any(DevolucaoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando bicicleta não existe")
    void deveRetornar400QuandoBicicletaNaoExiste() throws Exception {
        when(devolucaoService.devolverBicicleta(any(DevolucaoRequest.class)))
                .thenThrow(new IllegalArgumentException("Não existe bicicleta na tranca com ID:50"));

        mockMvc.perform(post("/devolucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devolucaoRequest)))
                .andExpect(status().isBadRequest());

        verify(devolucaoService, times(1)).devolverBicicleta(any(DevolucaoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando tranca não existe")
    void deveRetornar400QuandoTrancaNaoExiste() throws Exception {
        when(devolucaoService.devolverBicicleta(any(DevolucaoRequest.class)))
                .thenThrow(new IllegalArgumentException("Não existe tranca com ID: 100"));

        mockMvc.perform(post("/devolucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devolucaoRequest)))
                .andExpect(status().isBadRequest());

        verify(devolucaoService, times(1)).devolverBicicleta(any(DevolucaoRequest.class));
    }

    @Test
    @DisplayName("Deve processar devolução com requisição de reparo")
    void deveProcessarDevolucaoComRequisicaoReparo() throws Exception {
        DevolucaoRequest requestComReparo = new DevolucaoRequest(100L, 50L, true);

        when(devolucaoService.devolverBicicleta(any(DevolucaoRequest.class)))
                .thenReturn(aluguelResponse);

        mockMvc.perform(post("/devolucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestComReparo)))
                .andExpect(status().isOk());

        verify(devolucaoService, times(1)).devolverBicicleta(any(DevolucaoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando não existe aluguel em andamento")
    void deveRetornar400QuandoNaoExisteAluguelEmAndamento() throws Exception {
        when(devolucaoService.devolverBicicleta(any(DevolucaoRequest.class)))
                .thenThrow(new IllegalArgumentException("Não existe aluguel em andamento para esta bicicleta!"));

        mockMvc.perform(post("/devolucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devolucaoRequest)))
                .andExpect(status().isBadRequest());

        verify(devolucaoService, times(1)).devolverBicicleta(any(DevolucaoRequest.class));
    }
}

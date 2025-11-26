package com.es2.microservicos.controllers;

import com.es2.microservicos.dtos.requests.GerarAluguelRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.services.AluguelService;
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
@DisplayName("Testes para AluguelController")
class AluguelControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AluguelService aluguelService;

    @InjectMocks
    private AluguelController aluguelController;

    private GerarAluguelRequest gerarAluguelRequest;
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
                .standaloneSetup(aluguelController)
                .setControllerAdvice(new TestExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        gerarAluguelRequest = new GerarAluguelRequest(1L, 100L);

        aluguelResponse = new AluguelResponse(
                1L,
                1L,
                LocalDateTime.now(),
                5L,
                null,
                null,
                10.0,
                1L
        );
    }

    @Test
    @DisplayName("Deve gerar aluguel e retornar 200 OK")
    void deveGerarAluguelERetornar200() throws Exception {
        when(aluguelService.gerarAluguel(any(GerarAluguelRequest.class)))
                .thenReturn(aluguelResponse);

        mockMvc.perform(post("/aluguel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gerarAluguelRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bicicletaId").value(1))
                .andExpect(jsonPath("$.ciclistaId").value(1));

        verify(aluguelService, times(1)).gerarAluguel(any(GerarAluguelRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando dados são inválidos")
    void deveRetornar400QuandoDadosSaoInvalidos() throws Exception {
        when(aluguelService.gerarAluguel(any(GerarAluguelRequest.class)))
                .thenThrow(new IllegalArgumentException("Tranca inicial não existe!"));

        mockMvc.perform(post("/aluguel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gerarAluguelRequest)))
                .andExpect(status().isBadRequest());

        verify(aluguelService, times(1)).gerarAluguel(any(GerarAluguelRequest.class));
    }

    @Test
    @DisplayName("Deve processar requisição com tranca válida")
    void deveProcessarRequisicaoComTrancaValida() throws Exception {
        GerarAluguelRequest requestValido = new GerarAluguelRequest(5L, 200L);
        AluguelResponse responseValido = new AluguelResponse(
                1L, 2L, LocalDateTime.now(), 5L, null, null, 10.0, 200L
        );

        when(aluguelService.gerarAluguel(any(GerarAluguelRequest.class)))
                .thenReturn(responseValido);

        mockMvc.perform(post("/aluguel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bicicletaId").value(2))
                .andExpect(jsonPath("$.trancaInicio").value(5));

        verify(aluguelService, times(1)).gerarAluguel(any(GerarAluguelRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando ciclista já possui aluguel")
    void deveRetornar400QuandoCiclistaJaPossuiAluguel() throws Exception {
        when(aluguelService.gerarAluguel(any(GerarAluguelRequest.class)))
                .thenThrow(new IllegalArgumentException("O ciclista já possui um aluguel em andamento!"));

        mockMvc.perform(post("/aluguel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gerarAluguelRequest)))
                .andExpect(status().isBadRequest());

        verify(aluguelService, times(1)).gerarAluguel(any(GerarAluguelRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando bicicleta está em reparo")
    void deveRetornar400QuandoBicicletaEstaEmReparo() throws Exception {
        when(aluguelService.gerarAluguel(any(GerarAluguelRequest.class)))
                .thenThrow(new IllegalArgumentException("A bicicleta solicitada para uso está EM REPARO e não pode ser utilizada!"));

        mockMvc.perform(post("/aluguel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gerarAluguelRequest)))
                .andExpect(status().isBadRequest());

        verify(aluguelService, times(1)).gerarAluguel(any(GerarAluguelRequest.class));
    }
}

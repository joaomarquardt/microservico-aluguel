package com.es2.microservicos.controllers;

import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Nacionalidade;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.requests.AtualizarCiclistaRequest;
import com.es2.microservicos.dtos.requests.CriarCiclistaRequest;
import com.es2.microservicos.dtos.responses.BicicletaResponse;
import com.es2.microservicos.dtos.responses.CiclistaResponse;
import com.es2.microservicos.external.domain.BicicletaStatus;
import com.es2.microservicos.services.CiclistaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CiclistaController")
class CiclistaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CiclistaService ciclistaService;

    @InjectMocks
    private CiclistaController ciclistaController;

    private CriarCiclistaRequest criarRequest;
    private AtualizarCiclistaRequest atualizarRequest;
    private CiclistaResponse ciclistaResponse;
    private Ciclista ciclista;
    private BicicletaResponse bicicletaResponse;

    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(ciclistaController)
                .setControllerAdvice(new TestExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        AdicionarCartaoRequest cartaoRequest = new AdicionarCartaoRequest(
                "João Silva",
                "1234567890123456",
                LocalDate.of(2027, 12, 31),
                "123"
        );

        criarRequest = new CriarCiclistaRequest(
                "João Silva",
                Nacionalidade.BRASILEIRO,
                LocalDate.of(1990, 1, 1),
                "12345678901",
                "joao@email.com",
                "senha123",
                "senha123",
                null,
                cartaoRequest,
                "http://foto.com/doc.jpg"
        );

        atualizarRequest = new AtualizarCiclistaRequest(
                "João Silva Atualizado",
                Nacionalidade.BRASILEIRO,
                LocalDate.of(1990, 1, 1),
                "12345678901",
                "joao.atualizado@email.com",
                "senha123",
                "senha123",
                null,
                "http://foto.com/doc.jpg"
        );

        ciclistaResponse = new CiclistaResponse(
                1L,
                Status.INATIVO,
                "João Silva",
                LocalDate.of(1990, 1, 1),
                "12345678901",
                null,
                Nacionalidade.BRASILEIRO,
                "joao@email.com",
                "http://foto.com/doc.jpg"
        );

        ciclista = new Ciclista();
        ciclista.setId(1L);
        ciclista.setNome("João Silva");
        ciclista.setEmail("joao@email.com");
        ciclista.setStatus(Status.ATIVO);

        bicicletaResponse = new BicicletaResponse(
                10L, "Caloi", "Elite", "2023", 100, BicicletaStatus.EM_USO
        );
    }

    @Test
    @DisplayName("Deve obter ciclista por ID e retornar 200 OK")
    void deveObterCiclistaPorIdERetornar200() throws Exception {
        when(ciclistaService.obterCiclistaPorId(1L)).thenReturn(ciclista);

        mockMvc.perform(get("/ciclista/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        verify(ciclistaService, times(1)).obterCiclistaPorId(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 quando ciclista não encontrado")
    void deveRetornar404QuandoCiclistaNaoEncontrado() throws Exception {
        when(ciclistaService.obterCiclistaPorId(99L))
                .thenThrow(new EntityNotFoundException("Ciclista não encontrado!"));

        mockMvc.perform(get("/ciclista/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(ciclistaService, times(1)).obterCiclistaPorId(99L);
    }

    @Test
    @DisplayName("Deve criar ciclista e retornar 201 Created")
    void deveCriarCiclistaERetornar201() throws Exception {
        when(ciclistaService.criarCiclista(any(CriarCiclistaRequest.class)))
                .thenReturn(ciclistaResponse);

        mockMvc.perform(post("/ciclista")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        verify(ciclistaService, times(1)).criarCiclista(any(CriarCiclistaRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar ciclista com dados inválidos")
    void deveRetornar400AoCriarCiclistaComDadosInvalidos() throws Exception {
        when(ciclistaService.criarCiclista(any(CriarCiclistaRequest.class)))
                .thenThrow(new IllegalArgumentException("Email já cadastrado!"));

        mockMvc.perform(post("/ciclista")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequest)))
                .andExpect(status().isBadRequest());

        verify(ciclistaService, times(1)).criarCiclista(any(CriarCiclistaRequest.class));
    }

    @Test
    @DisplayName("Deve atualizar ciclista e retornar 200 OK")
    void deveAtualizarCiclistaERetornar200() throws Exception {
        when(ciclistaService.atualizarCiclista(eq(1L), any(AtualizarCiclistaRequest.class)))
                .thenReturn(ciclistaResponse);

        mockMvc.perform(put("/ciclista/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(ciclistaService, times(1)).atualizarCiclista(eq(1L), any(AtualizarCiclistaRequest.class));
    }

    @Test
    @DisplayName("Deve ativar ciclista e retornar 200 OK")
    void deveAtivarCiclistaERetornar200() throws Exception {
        when(ciclistaService.ativarCiclista(1L)).thenReturn(ciclistaResponse);

        mockMvc.perform(post("/ciclista/{id}/ativar", 1L))
                .andExpect(status().isOk());

        verify(ciclistaService, times(1)).ativarCiclista(1L);
    }

    @Test
    @DisplayName("Deve verificar permissão de aluguel e retornar 200 OK")
    void deveVerificarPermissaoAluguelERetornar200() throws Exception {
        when(ciclistaService.verificarPermissaoAluguel(1L)).thenReturn(true);

        mockMvc.perform(get("/ciclista/{id}/permiteAluguel", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(ciclistaService, times(1)).verificarPermissaoAluguel(1L);
    }

    @Test
    @DisplayName("Deve obter bicicleta alugada do ciclista")
    void deveObterBicicletaAlugadaDoCiclista() throws Exception {
        when(ciclistaService.obterBicicletaAlugadaPorIdCiclista(1L))
                .thenReturn(bicicletaResponse);

        mockMvc.perform(get("/ciclista/{id}/bicicletaAlugada", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));

        verify(ciclistaService, times(1)).obterBicicletaAlugadaPorIdCiclista(1L);
    }

    @Test
    @DisplayName("Deve verificar existência de email e retornar true")
    void deveVerificarExistenciaEmailERetornarTrue() throws Exception {
        when(ciclistaService.verificarExistenciaEmail("joao@email.com")).thenReturn(true);

        mockMvc.perform(get("/ciclista/existeEmail/{email}", "joao@email.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(ciclistaService, times(1)).verificarExistenciaEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve retornar false quando email não existe")
    void deveRetornarFalseQuandoEmailNaoExiste() throws Exception {
        when(ciclistaService.verificarExistenciaEmail("naoexiste@email.com")).thenReturn(false);

        mockMvc.perform(get("/ciclista/existeEmail/{email}", "naoexiste@email.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(ciclistaService, times(1)).verificarExistenciaEmail("naoexiste@email.com");
    }
}

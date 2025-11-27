package com.es2.microservicos.controllers;

import com.es2.microservicos.services.RestaurarBancoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para RestaurarBancoController")
class RestaurarBancoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestaurarBancoService restaurarBancoService;

    @InjectMocks
    private RestaurarBancoController restaurarBancoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(restaurarBancoController)
                .build();
    }

    @Test
    @DisplayName("Deve restaurar banco e retornar 200 OK")
    void deveRestaurarBancoERetornar200() throws Exception {
        doNothing().when(restaurarBancoService).restaurarBanco();
        mockMvc.perform(get("/restaurarBanco"))
                .andExpect(status().isOk());
        verify(restaurarBancoService, times(1)).restaurarBanco();
    }

    @Test
    @DisplayName("Deve chamar serviço de restauração ao receber requisição GET")
    void deveChamarServicoDeRestauracaoAoReceberRequisicaoGet() throws Exception {
        doNothing().when(restaurarBancoService).restaurarBanco();
        mockMvc.perform(get("/restaurarBanco"))
                .andExpect(status().isOk());
        verify(restaurarBancoService, times(1)).restaurarBanco();
    }

    @Test
    @DisplayName("Deve processar múltiplas requisições de restauração")
    void deveProcessarMultiplasRequisicoesDeRestauracao() throws Exception {
        doNothing().when(restaurarBancoService).restaurarBanco();
        mockMvc.perform(get("/restaurarBanco")).andExpect(status().isOk());
        mockMvc.perform(get("/restaurarBanco")).andExpect(status().isOk());
        mockMvc.perform(get("/restaurarBanco")).andExpect(status().isOk());
        verify(restaurarBancoService, times(3)).restaurarBanco();
    }

    @Test
    @DisplayName("Deve retornar 405 quando método não é GET")
    void deveRetornar405QuandoMetodoNaoEhGet() throws Exception {
        mockMvc.perform(post("/restaurarBanco"))
                .andExpect(status().isMethodNotAllowed());
        verify(restaurarBancoService, never()).restaurarBanco();
    }

    @Test
    @DisplayName("Deve executar restauração sem conteúdo no corpo da resposta")
    void deveExecutarRestauracaoSemConteudoNoCorpoDaResposta() throws Exception {
        doNothing().when(restaurarBancoService).restaurarBanco();
        mockMvc.perform(get("/restaurarBanco"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(restaurarBancoService, times(1)).restaurarBanco();
    }
}

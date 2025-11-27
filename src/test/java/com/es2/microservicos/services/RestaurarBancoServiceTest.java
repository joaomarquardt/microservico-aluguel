package com.es2.microservicos.services;

import com.es2.microservicos.repositories.AluguelRepository;
import com.es2.microservicos.repositories.CartaoDeCreditoRepository;
import com.es2.microservicos.repositories.CiclistaRepository;
import com.es2.microservicos.repositories.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para RestaurarBancoService")
class RestaurarBancoServiceTest {

    @Mock
    private AluguelRepository aluguelRepository;

    @Mock
    private CartaoDeCreditoRepository cartaoDeCreditoRepository;

    @Mock
    private CiclistaRepository ciclistaRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private RestaurarBancoService restaurarBancoService;

    @BeforeEach
    void setUp() {
        doNothing().when(aluguelRepository).deleteAll();
        doNothing().when(cartaoDeCreditoRepository).deleteAll();
        doNothing().when(ciclistaRepository).deleteAll();
        doNothing().when(funcionarioRepository).deleteAll();
    }

    @Test
    @DisplayName("Deve restaurar banco de dados deletando todas as entidades")
    void deveRestaurarBancoDeDadosDeletandoTodasEntidades() {
        assertDoesNotThrow(() -> restaurarBancoService.restaurarBanco());
        verify(aluguelRepository, times(1)).deleteAll();
        verify(cartaoDeCreditoRepository, times(1)).deleteAll();
        verify(ciclistaRepository, times(1)).deleteAll();
        verify(funcionarioRepository, times(1)).deleteAll();
    }

    @Test
    @DisplayName("Deve deletar na ordem correta para respeitar constraints")
    void deveDeletarNaOrdemCorretaParaRespeitarConstraints() {
        restaurarBancoService.restaurarBanco();
        var inOrder = inOrder(aluguelRepository, cartaoDeCreditoRepository, ciclistaRepository, funcionarioRepository);
        inOrder.verify(aluguelRepository).deleteAll();
        inOrder.verify(cartaoDeCreditoRepository).deleteAll();
        inOrder.verify(ciclistaRepository).deleteAll();
        inOrder.verify(funcionarioRepository).deleteAll();
    }

    @Test
    @DisplayName("Deve executar restauração múltiplas vezes sem erro")
    void deveExecutarRestauracaoMultiplasVezesSemErro() {
        assertDoesNotThrow(() -> {
            restaurarBancoService.restaurarBanco();
            restaurarBancoService.restaurarBanco();
            restaurarBancoService.restaurarBanco();
        });
        verify(aluguelRepository, times(3)).deleteAll();
        verify(cartaoDeCreditoRepository, times(3)).deleteAll();
        verify(ciclistaRepository, times(3)).deleteAll();
        verify(funcionarioRepository, times(3)).deleteAll();
    }
}

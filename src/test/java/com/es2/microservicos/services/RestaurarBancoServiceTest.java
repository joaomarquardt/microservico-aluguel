package com.es2.microservicos.services;

import com.es2.microservicos.domain.*;
import com.es2.microservicos.repositories.AluguelRepository;
import com.es2.microservicos.repositories.CartaoDeCreditoRepository;
import com.es2.microservicos.repositories.CiclistaRepository;
import com.es2.microservicos.repositories.FuncionarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query nativeQuery;

    @InjectMocks
    private RestaurarBancoService restaurarBancoService;

    @BeforeEach
    void setUp() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.executeUpdate()).thenReturn(1);
        when(ciclistaRepository.findAll()).thenReturn(List.of(
                criarCiclistaMock(1L),
                criarCiclistaMock(2L),
                criarCiclistaMock(3L),
                criarCiclistaMock(4L)
        ));
    }

    @Test
    @DisplayName("Deve restaurar banco de dados deletando todas as entidades")
    void deveRestaurarBancoDeDadosDeletandoTodasEntidades() {
        assertDoesNotThrow(() -> restaurarBancoService.restaurarBanco());
        verify(entityManager, times(1)).createNativeQuery(anyString());
        verify(nativeQuery, times(1)).executeUpdate();
        verify(ciclistaRepository, times(1)).saveAll(any());
        verify(cartaoDeCreditoRepository, times(1)).saveAll(any());
        verify(funcionarioRepository, times(1)).saveAll(any());
        verify(aluguelRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Deve executar na ordem correta: reset depois popular")
    void deveExecutarNaOrdemCorretaResetDepoisPopular() {
        restaurarBancoService.restaurarBanco();
        var inOrder = inOrder(entityManager, ciclistaRepository, cartaoDeCreditoRepository,
                funcionarioRepository, aluguelRepository);
        inOrder.verify(entityManager).createNativeQuery(anyString());
        inOrder.verify(ciclistaRepository).saveAll(any());
        inOrder.verify(cartaoDeCreditoRepository).saveAll(any());
        inOrder.verify(funcionarioRepository).saveAll(any());
        inOrder.verify(aluguelRepository).saveAll(any());
    }

    @Test
    @DisplayName("Deve executar restauração múltiplas vezes sem erro")
    void deveExecutarRestauracaoMultiplasVezesSemErro() {
        assertDoesNotThrow(() -> {
            restaurarBancoService.restaurarBanco();
            restaurarBancoService.restaurarBanco();
            restaurarBancoService.restaurarBanco();
        });

        verify(entityManager, times(3)).createNativeQuery(anyString());
        verify(ciclistaRepository, times(3)).saveAll(any());
        verify(cartaoDeCreditoRepository, times(3)).saveAll(any());
        verify(funcionarioRepository, times(3)).saveAll(any());
        verify(aluguelRepository, times(3)).saveAll(any());
    }

    private Ciclista criarCiclistaMock(Long id) {
        Ciclista ciclista = new Ciclista();
        ciclista.setId(id);
        return ciclista;
    }
}

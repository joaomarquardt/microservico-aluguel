package com.es2.microservicos.services;

import com.es2.microservicos.domain.*;
import com.es2.microservicos.repositories.AluguelRepository;
import com.es2.microservicos.repositories.CartaoDeCreditoRepository;
import com.es2.microservicos.repositories.CiclistaRepository;
import com.es2.microservicos.repositories.FuncionarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RestaurarBancoService {
    private final AluguelRepository aluguelRepository;
    private final CartaoDeCreditoRepository cartaoDeCreditoRepository;
    private final CiclistaRepository ciclistaRepository;
    private final FuncionarioRepository funcionarioRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    public RestaurarBancoService(AluguelRepository aluguelRepository, CartaoDeCreditoRepository cartaoDeCreditoRepository, CiclistaRepository ciclistaRepository, FuncionarioRepository funcionarioRepository, EntityManager entityManager) {
        this.aluguelRepository = aluguelRepository;
        this.cartaoDeCreditoRepository = cartaoDeCreditoRepository;
        this.ciclistaRepository = ciclistaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.entityManager = entityManager;
    }

    public void restaurarBanco() {
        resetarBanco();
        popularBanco();
    }

    public void resetarBanco() {
        entityManager.createNativeQuery(
                "TRUNCATE TABLE alugueis, cartoes_de_credito, ciclistas, funcionarios, passaportes RESTART IDENTITY CASCADE"
        ).executeUpdate();
    }

    public void popularBanco() {
        criarCiclistas();
        criarCartoesDeCredito();
        criarFuncionarios();
        criarAlugueis();
    }

    public void criarCiclistas() {
        Ciclista ciclista1 = new Ciclista();
        ciclista1.setNome("Fulano Beltrano");
        ciclista1.setNascimento(LocalDate.of(2021, 5, 2));
        ciclista1.setCpf("78804034009");
        ciclista1.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclista1.setEmail("user@example.com");
        ciclista1.setSenha("ABC123");
        ciclista1.setStatus(Status.ATIVO);

        Ciclista ciclista2 = new Ciclista();
        ciclista2.setNome("Fulano Beltrano");
        ciclista2.setNascimento(LocalDate.of(2021, 5, 2));
        ciclista2.setCpf("43943488039");
        ciclista2.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclista2.setEmail("user2@example.com");
        ciclista2.setSenha("ABC123");
        ciclista2.setStatus(Status.AGUARDANDO_CONFIRMACAO);

        Ciclista ciclista3 = new Ciclista();
        ciclista3.setNome("Fulano Beltrano");
        ciclista3.setNascimento(LocalDate.of(2021, 5, 2));
        ciclista3.setCpf("10243164084");
        ciclista3.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclista3.setEmail("user3@example.com");
        ciclista3.setSenha("ABC123");
        ciclista3.setStatus(Status.ATIVO);

        Ciclista ciclista4 = new Ciclista();
        ciclista4.setNome("Fulano Beltrano");
        ciclista4.setNascimento(LocalDate.of(2021, 5, 2));
        ciclista4.setCpf("30880150017");
        ciclista4.setNacionalidade(Nacionalidade.BRASILEIRO);
        ciclista4.setEmail("user4@example.com");
        ciclista4.setSenha("ABC123");
        ciclista4.setStatus(Status.ATIVO);

        List<Ciclista> ciclistas = List.of(ciclista1, ciclista2, ciclista3, ciclista4);

        ciclistaRepository.saveAll(ciclistas);
    }

    public void criarCartoesDeCredito() {
        CartaoDeCredito cartaoDeCredito1 = new CartaoDeCredito();
        cartaoDeCredito1.setNomeTitular("Fulano Beltrano");
        cartaoDeCredito1.setNumero("4012001037141112");
        cartaoDeCredito1.setValidade(LocalDate.of(2022, 12, 1));
        cartaoDeCredito1.setCvv("132");

        List<CartaoDeCredito> cartoesDeCredito = List.of(cartaoDeCredito1);

        cartaoDeCreditoRepository.saveAll(cartoesDeCredito);
    }

    public void criarFuncionarios() {
        Funcionario funcionario1 = new Funcionario();
        funcionario1.setMatricula("12345");
        funcionario1.setSenha("123");
        funcionario1.setConfirmacaoSenha("123");
        funcionario1.setEmail("employee@example.com");
        funcionario1.setNome("Beltrano");
        funcionario1.setIdade(25);
        funcionario1.setFuncao(Funcao.REPARADOR);
        funcionario1.setCpf("99999999999");

        List<Funcionario> funcionarios = List.of(funcionario1);

        funcionarioRepository.saveAll(funcionarios);
    }

    public void criarAlugueis() {
        List<Ciclista> ciclistas = ciclistaRepository.findAll();

        Aluguel aluguel1 = new Aluguel();
        aluguel1.setCiclista(ciclistas.get(2));
        aluguel1.setBicicletaId(3L);
        aluguel1.setTrancaInicio(2L);
        aluguel1.setCobranca(1);
        aluguel1.setHoraInicio(LocalDateTime.now());

        Aluguel aluguel2 = new Aluguel();
        aluguel2.setCiclista(ciclistas.get(3));
        aluguel2.setBicicletaId(5L);
        aluguel2.setTrancaInicio(4L);
        aluguel2.setCobranca(2);
        aluguel2.setHoraInicio(LocalDateTime.now().minusHours(2));

        Aluguel aluguel3 = new Aluguel();
        aluguel3.setCiclista(ciclistas.get(2));
        aluguel3.setBicicletaId(1L);
        aluguel3.setTrancaInicio(1L);
        aluguel3.setTrancaFim(2L);
        aluguel3.setCobranca(3);
        aluguel3.setHoraInicio(LocalDateTime.now().minusHours(2));
        aluguel3.setHoraInicio(LocalDateTime.now());

        List<Aluguel> alugueis = List.of(aluguel1, aluguel2, aluguel3);

        aluguelRepository.saveAll(alugueis);
    }
}

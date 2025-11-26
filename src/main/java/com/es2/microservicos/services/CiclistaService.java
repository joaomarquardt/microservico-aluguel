package com.es2.microservicos.services;

import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Nacionalidade;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.AtualizarCiclistaRequest;
import com.es2.microservicos.dtos.requests.CriarCiclistaRequest;
import com.es2.microservicos.dtos.responses.BicicletaResponse;
import com.es2.microservicos.dtos.responses.CiclistaResponse;
import com.es2.microservicos.external.gateways.EquipamentoServiceGateway;
import com.es2.microservicos.external.gateways.ExternoServiceGateway;
import com.es2.microservicos.mappers.CiclistaMapper;
import com.es2.microservicos.repositories.CiclistaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CiclistaService {
    private final CiclistaMapper ciclistaMapper;
    private final CiclistaRepository ciclistaRepository;
    private final CartaoDeCreditoService cartaoService;
    private final ExternoServiceGateway externoServiceGateway;
    private final EquipamentoServiceGateway equipamentoServiceGateway;

    public CiclistaService(CiclistaRepository ciclistaRepository, CiclistaMapper ciclistaMapper, CartaoDeCreditoService cartaoService, ExternoServiceGateway externoServiceGateway, EquipamentoServiceGateway equipamentoServiceGateway) {
        this.ciclistaRepository = ciclistaRepository;
        this.ciclistaMapper = ciclistaMapper;
        this.cartaoService = cartaoService;
        this.externoServiceGateway = externoServiceGateway;
        this.equipamentoServiceGateway = equipamentoServiceGateway;
    }

    public Ciclista obterCiclistaPorId(Long id) {
        return ciclistaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ciclista não encontrado!"));
    }

    public CiclistaResponse criarCiclista(CriarCiclistaRequest ciclistaRequest) {
        Boolean existeEmail = verificarExistenciaEmail(ciclistaRequest.email());
        if (existeEmail) {
            throw new IllegalArgumentException("Email já cadastrado!");
        }
        if (ciclistaRequest.nacionalidade() == Nacionalidade.BRASILEIRO && ciclistaRequest.cpf() == null) {
            throw new IllegalArgumentException("Ciclistas brasileiros devem fornecer CPF!");
        }
        if (ciclistaRequest.nacionalidade() == Nacionalidade.ESTRANGEIRO && (ciclistaRequest.passaporte() == null || ciclistaRequest.passaporte().pais().isBlank())) {
            throw new IllegalArgumentException("Ciclistas estrangeiros devem fornecer passaporte!");
        }
        if (!ciclistaRequest.senha().equals(ciclistaRequest.confirmacaoSenha())) {
            throw new IllegalArgumentException("Senha e confirmação de senha não coincidem!");
        }
        ResponseEntity validacaoCartaoResponse = externoServiceGateway.validacaoCartaoDeCredito(ciclistaRequest.cartaoDeCredito());
        if (validacaoCartaoResponse.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException("Cartão de crédito inválido!");
        }
        Ciclista ciclista = ciclistaMapper.toCiclista(ciclistaRequest);
        ciclista.setStatus(Status.INATIVO);
        Ciclista ciclistaSalvo = ciclistaRepository.save(ciclista);
        cartaoService.cadastrarCartaoDeCredito(ciclistaRequest.cartaoDeCredito(), ciclistaSalvo);
        externoServiceGateway.confirmacaoCadastroEmail(ciclistaSalvo.getNome(), ciclistaSalvo.getEmail());
        return ciclistaMapper.toCiclistaResponse(ciclistaSalvo);
    }

    public CiclistaResponse atualizarCiclista(Long id, AtualizarCiclistaRequest ciclistaRequest) {
        Ciclista ciclista = obterCiclistaPorId(id);
        // TODO: Separar lógicas abaixo em um método privado para reutilização
        if (ciclistaRequest.nacionalidade() == Nacionalidade.BRASILEIRO && ciclistaRequest.cpf() == null) {
            throw new IllegalArgumentException("Ciclistas brasileiros devem fornecer CPF!");
        }
        if (ciclistaRequest.nacionalidade() == Nacionalidade.ESTRANGEIRO && (ciclistaRequest.passaporte() == null || ciclistaRequest.passaporte().pais().isBlank())) {
            throw new IllegalArgumentException("Ciclistas estrangeiros devem fornecer passaporte!");
        }
        if (!ciclistaRequest.senha().equals(ciclistaRequest.confirmacaoSenha())) {
            throw new IllegalArgumentException("Senha e confirmação de senha não coincidem!");
        }
        ciclistaMapper.updateCiclistaFromRequest(ciclistaRequest, ciclista);
        Ciclista ciclistaAtualizado = ciclistaRepository.save(ciclista);
        ResponseEntity atualizacaoCiclistaEmailResponse = externoServiceGateway.atualizacaoCiclistaEmail(ciclistaAtualizado.getNome(), ciclistaAtualizado.getEmail());
        if (atualizacaoCiclistaEmailResponse.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException("Erro ao enviar email de atualização de ciclista!");
        }
        return ciclistaMapper.toCiclistaResponse(ciclistaAtualizado);
    }

    public CiclistaResponse ativarCiclista(Long id) {
        Ciclista ciclista = obterCiclistaPorId(id);
        if (ciclista.getStatus() == Status.ATIVO) {
            throw new IllegalArgumentException("Ciclista já está ativo!");
        }
        ciclista.setStatus(Status.ATIVO);
        // TODO: Verificar se passo 3 do UC02 de registro da data/hora deve ser implementado
        Ciclista ciclistaSalvo = ciclistaRepository.save(ciclista);
        return ciclistaMapper.toCiclistaResponse(ciclistaSalvo);
    }


    public Boolean verificarExistenciaEmail(String email) {
        return ciclistaRepository.existsByEmail(email);
    }
}

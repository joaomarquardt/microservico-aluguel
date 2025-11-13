package com.es2.microservicos.services;

import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Nacionalidade;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.dtos.requests.AtualizarCiclistaRequest;
import com.es2.microservicos.dtos.requests.CriarCiclistaRequest;
import com.es2.microservicos.dtos.responses.CiclistaResponse;
import com.es2.microservicos.mappers.CiclistaMapper;
import com.es2.microservicos.repositories.CiclistaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CiclistaService {
    private CiclistaMapper ciclistaMapper;
    private CiclistaRepository ciclistaRepository;

    public CiclistaService(CiclistaRepository ciclistaRepository, CiclistaMapper ciclistaMapper) {
        this.ciclistaRepository = ciclistaRepository;
        this.ciclistaMapper = ciclistaMapper;
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
        // TODO: Validação de cartão de crédito via microserviço Externo (Passo 7 - UC01)
        Ciclista ciclista = ciclistaMapper.toCiclista(ciclistaRequest);
        ciclista.setStatus(Status.INATIVO);
        Ciclista ciclistaSalvo = ciclistaRepository.save(ciclista);
        // TODO: Email de confirmação de cadastro via microserviço Externo (Passo 9 - UC01)
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
        // TODO: Email de atualização de dados via microserviço Externo (Passo 4 - UC06)
        return ciclistaMapper.toCiclistaResponse(ciclistaAtualizado);
    }

    public CiclistaResponse ativarCiclista(Long id) {
        Ciclista ciclista = obterCiclistaPorId(id);
        if (ciclista.getStatus() == Status.ATIVO) {
            throw new IllegalArgumentException("Ciclista já está ativo!");
        }
        ciclista.setStatus(Status.ATIVO);
        Ciclista ciclistaSalvo = ciclistaRepository.save(ciclista);
        return ciclistaMapper.toCiclistaResponse(ciclistaSalvo);
    }

    // TODO: Implementar método verificarPermissaoAluguel
    public Boolean verificarPermissaoAluguel(Long id) {
        return null;
    }

    // TODO: Terminar implementação de método obterBicicletaAlugada conectando com microserviço de Equipamento
    public ResponseEntity<?> obterBicicletaAlugada(Long id) {
        return null;
    }

    public Boolean verificarExistenciaEmail(String email) {
        return ciclistaRepository.existsByEmail(email);
    }


}

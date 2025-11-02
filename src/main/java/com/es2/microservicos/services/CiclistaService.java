package com.es2.microservicos.services;

import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.domain.Status;
import com.es2.microservicos.repositories.CiclistaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CiclistaService {
    private AluguelService aluguelService;
    private CiclistaRepository ciclistaRepository;

    public CiclistaService(CiclistaRepository ciclistaRepository) {
        this.ciclistaRepository = ciclistaRepository;
    }

    public Ciclista obterCiclistaPorId(Long id) {
        return ciclistaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ciclista não encontrado!"));
    }

    public Ciclista atualizarCiclista(Long id, Ciclista ciclistaDetalhes) {
        Ciclista ciclistaExistente = ciclistaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ciclista não encontrado!"));
        ciclistaExistente.setNome(ciclistaDetalhes.getNome());
        ciclistaExistente.setEmail(ciclistaDetalhes.getEmail());
        ciclistaExistente.setStatus(ciclistaDetalhes.getStatus());
        ciclistaExistente.setCpf(ciclistaDetalhes.getCpf());
        ciclistaExistente.setNascimento(ciclistaDetalhes.getNascimento());
        ciclistaExistente.setNacionalidade(ciclistaDetalhes.getNacionalidade());
        ciclistaExistente.setPassaporte(ciclistaDetalhes.getPassaporte());
        ciclistaExistente.setUrlFotoDocumento(ciclistaDetalhes.getUrlFotoDocumento());
        return ciclistaRepository.save(ciclistaExistente);
    }

    public Ciclista ativarCiclista(Long id) {
        Ciclista ciclistaExistente = ciclistaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ciclista não encontrado!"));
        ciclistaExistente.setStatus(Status.ATIVO);
        return ciclistaRepository.save(ciclistaExistente);
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

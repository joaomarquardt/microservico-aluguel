package com.es2.microservicos.services;

import com.es2.microservicos.domain.CartaoDeCredito;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.responses.CartaoResponse;
import com.es2.microservicos.external.gateways.ExternoServiceGateway;
import com.es2.microservicos.mappers.CartaoDeCreditoMapper;
import com.es2.microservicos.repositories.CartaoDeCreditoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CartaoDeCreditoService {
    private final CartaoDeCreditoRepository cartaoDeCreditoRepository;
    private final CartaoDeCreditoMapper cartaoMapper;
    private final ExternoServiceGateway externoServiceGateway;

    public CartaoDeCreditoService(CartaoDeCreditoRepository cartaoDeCreditoRepository, CartaoDeCreditoMapper cartaoMapper, ExternoServiceGateway externoServiceGateway) {
        this.cartaoDeCreditoRepository = cartaoDeCreditoRepository;
        this.cartaoMapper = cartaoMapper;
        this.externoServiceGateway = externoServiceGateway;
    }

    public CartaoResponse cadastrarCartaoDeCredito(AdicionarCartaoRequest cartaoRequest, Ciclista ciclista) {
        CartaoDeCredito cartao = cartaoMapper.toCartaoDeCredito(cartaoRequest);
        cartao.setCiclista(ciclista);
        CartaoDeCredito cartaoSalvo = cartaoDeCreditoRepository.save(cartao);
        return cartaoMapper.toCartaoResponse(cartaoSalvo);
    }

    public CartaoDeCredito obterEntidadeCartaoDeCreditoPorIdCiclista(Long idCiclista) {
        return cartaoDeCreditoRepository.findByCiclistaId(idCiclista).orElseThrow(() -> new EntityNotFoundException("Cartão de crédito não encontrado para ciclista com ID: " + idCiclista));
    }
    
    public CartaoResponse obterCartaoDeCreditoPorIdCiclista(Long idCiclista) {
        CartaoDeCredito cartao = obterEntidadeCartaoDeCreditoPorIdCiclista(idCiclista);
        return cartaoMapper.toCartaoResponse(cartao);
    }

    public void atualizarCartaoDeCreditoPorIdCiclista(Long idCiclista, AdicionarCartaoRequest cartaoRequest) {
        CartaoDeCredito cartaoExistente = obterEntidadeCartaoDeCreditoPorIdCiclista(idCiclista);
        externoServiceGateway.validacaoCartaoDeCredito(cartaoRequest);
        cartaoMapper.updateCartaoDeCreditoFromRequest(cartaoRequest, cartaoExistente);
        cartaoDeCreditoRepository.save(cartaoExistente);
        Ciclista ciclista = cartaoExistente.getCiclista();
        externoServiceGateway.atualizacaoCartaoEmail(ciclista.getEmail());
    }
}

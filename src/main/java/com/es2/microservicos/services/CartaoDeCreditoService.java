package com.es2.microservicos.services;

import com.es2.microservicos.domain.CartaoDeCredito;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.responses.CartaoResponse;
import com.es2.microservicos.gateways.ExternoServiceGateway;
import com.es2.microservicos.mappers.CartaoDeCreditoMapper;
import com.es2.microservicos.repositories.CartaoDeCreditoRepository;
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


    // TODO: Implementar lógica de negócio para gerenciar cartões de crédito
    public CartaoDeCredito obterCartaoDeCreditoPorIdCiclista(Long idCiclista) {
        return cartaoDeCreditoRepository.findByCiclistaId(idCiclista);
    }


    public void atualizarCartaoDeCreditoPorIdCiclista(Long idCiclista, AdicionarCartaoRequest cartaoRequest) {
        CartaoDeCredito cartaoExistente = cartaoDeCreditoRepository.findByCiclistaId(idCiclista);
        if (cartaoExistente == null) {
            throw new IllegalArgumentException("Cartão de crédito não encontrado para o ciclista com ID: " + idCiclista);
        }
        externoServiceGateway.validacaoCartaoDeCredito(cartaoRequest);
        cartaoMapper.updateCartaoDeCreditoFromRequest(cartaoRequest, cartaoExistente);
        cartaoDeCreditoRepository.save(cartaoExistente);
        Ciclista ciclista = cartaoExistente.getCiclista();
        externoServiceGateway.atualizacaoCartaoEmail(ciclista.getNome(), ciclista.getEmail());
    }
}

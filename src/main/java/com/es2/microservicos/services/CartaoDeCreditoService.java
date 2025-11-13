package com.es2.microservicos.services;

import com.es2.microservicos.domain.CartaoDeCredito;
import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.responses.CartaoResponse;
import com.es2.microservicos.mappers.CartaoDeCreditoMapper;
import com.es2.microservicos.repositories.CartaoDeCreditoRepository;
import org.springframework.stereotype.Service;

@Service
public class CartaoDeCreditoService {
    private CartaoDeCreditoRepository cartaoDeCreditoRepository;
    private CartaoDeCreditoMapper cartaoMapper;

    public CartaoDeCreditoService(CartaoDeCreditoRepository cartaoDeCreditoRepository, CartaoDeCreditoMapper cartaoMapper) {
        this.cartaoDeCreditoRepository = cartaoDeCreditoRepository;
        this.cartaoMapper = cartaoMapper;
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
        // TODO: Validar dados do cartão de crédito no microserviço Externo (Passo 3 - UC07)
        cartaoMapper.updateCartaoDeCreditoFromRequest(cartaoRequest, cartaoExistente);
        cartaoDeCreditoRepository.save(cartaoExistente);
        // TODO: Notificar email do Ciclista sobre atualização do cartão de crédito no microserviço Externo (Passo 5 - UC07)
    }
}

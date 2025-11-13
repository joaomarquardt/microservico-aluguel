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


    public CartaoDeCredito atualizarCartaoDeCreditoPorIdCiclista(Long idCiclista, CartaoDeCredito cartaoDeCreditoDetalhes) {
        CartaoDeCredito cartaoDeCredito = cartaoDeCreditoRepository.findByCiclistaId(idCiclista);
        cartaoDeCredito.setNomeTitular(cartaoDeCreditoDetalhes.getNomeTitular());
        cartaoDeCredito.setNumeroCartao(cartaoDeCreditoDetalhes.getNumeroCartao());
        cartaoDeCredito.setDataValidade(cartaoDeCreditoDetalhes.getDataValidade());
        cartaoDeCredito.setCodigoSeguranca(cartaoDeCreditoDetalhes.getCodigoSeguranca());
        return cartaoDeCreditoRepository.save(cartaoDeCredito);
    }
}

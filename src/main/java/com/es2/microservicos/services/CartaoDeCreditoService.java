package com.es2.microservicos.services;

import com.es2.microservicos.domain.CartaoDeCredito;
import com.es2.microservicos.repositories.CartaoDeCreditoRepository;
import org.springframework.stereotype.Service;

@Service
public class CartaoDeCreditoService {
    private CartaoDeCreditoRepository cartaoDeCreditoRepository;

    public CartaoDeCreditoService(CartaoDeCreditoRepository cartaoDeCreditoRepository) {
        this.cartaoDeCreditoRepository = cartaoDeCreditoRepository;
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

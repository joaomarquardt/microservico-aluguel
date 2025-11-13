package com.es2.microservicos.gateways;

import com.es2.microservicos.dtos.responses.BicicletaResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EquipamentoServiceGateway {
    private final RestClient restClient;

    public EquipamentoServiceGateway(@Qualifier("restClientEquipamento") RestClient restClient) {
        this.restClient = restClient;
    }

    public BicicletaResponse obterBicicletaPorId(Long idCiclista) {
        return null;
    }

    public BicicletaResponse obterBicicletaPorIdTranca(Long idTranca) {
        return null;
    }

    public boolean existeTrancaPorId(Long idTranca) {
        return true;
    }
}

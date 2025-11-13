package com.es2.microservicos.gateways;

import com.es2.microservicos.dtos.responses.BicicletaResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EquipamentoServiceGateway {
    private RestClient restClient;

    public EquipamentoServiceGateway(RestClient restClient) {
        this.restClient = RestClient.builder()
                .baseUrl("http://equipamento-microservico/api") // TODO: Definir a URL base do microserviço Equipamento
                .build();
    }

    public BicicletaResponse obterBicicletaPorId(Long idCiclista) {
        return null;
    }
}

package com.es2.microservicos.external.gateways;

import com.es2.microservicos.dtos.responses.BicicletaResponse;
import com.es2.microservicos.dtos.responses.TrancaResponse;
import com.es2.microservicos.external.domain.BicicletaStatus;
import com.es2.microservicos.external.domain.TrancaStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EquipamentoServiceGateway {
    private final RestClient restClient;

    public EquipamentoServiceGateway(@Qualifier("restClientEquipamento") RestClient restClient) {
        this.restClient = restClient;
    }

    public BicicletaResponse obterBicicletaPorId(Long idCiclista) {
        ResponseEntity<BicicletaResponse> bicicletaResponse = restClient.get()
                .uri("/bicicleta/{idCiclista}", idCiclista)
                .retrieve()
                .toEntity(BicicletaResponse.class);
        if (!bicicletaResponse.hasBody()) {
            throw new IllegalArgumentException("Não existe bicicleta com ID: " + idCiclista);
        }
        return bicicletaResponse.getBody();
    }

    public BicicletaResponse obterBicicletaPorIdTranca(Long idTranca) {
        ResponseEntity<BicicletaResponse> bicicletaTrancaResponse = restClient.get()
                .uri("/tranca/{idTranca}/bicicleta", idTranca)
                .retrieve()
                .toEntity(BicicletaResponse.class);
        if (!bicicletaTrancaResponse.hasBody()) {
            throw new IllegalArgumentException("Não existe bicicleta na tranca com ID: " + idTranca);
        }
        return bicicletaTrancaResponse.getBody();
    }

    public boolean existeTrancaPorId(Long idTranca) {
        if (idTranca == null) {
            throw new IllegalArgumentException("ID da tranca não pode ser nulo.");
        }
        ResponseEntity tranca = restClient.get()
                .uri("/tranca/{idTranca}", idTranca)
                .retrieve()
                .toEntity(Boolean.class);
        return tranca.getStatusCode().is2xxSuccessful();
    }

     public BicicletaResponse alterarStatusBicicleta(Long idBicicleta, BicicletaStatus status) {
        ResponseEntity<BicicletaResponse> bicicletaResponse = restClient.put()
                .uri("/bicicleta/{idBicicleta}/status/{status}", idBicicleta, status)
                .retrieve()
                .toEntity(BicicletaResponse.class);
        if (!bicicletaResponse.hasBody()) {
            throw new IllegalArgumentException("Não foi possível alterar o status da bicicleta com ID: " + idBicicleta);
        }
        return bicicletaResponse.getBody();
     }

    public TrancaResponse alterarStatusTranca(Long idTranca, TrancaStatus status) {
        ResponseEntity<TrancaResponse> trancaResponse = restClient.post()
                .uri("/tranca/{idTranca}/status/{status}", idTranca, status)
                .retrieve()
                .toEntity(TrancaResponse.class);
        if (!trancaResponse.hasBody()) {
            throw new IllegalArgumentException("Não foi possível alterar o status da tranca com ID: " + idTranca);
        }
        return trancaResponse.getBody();
    }
}

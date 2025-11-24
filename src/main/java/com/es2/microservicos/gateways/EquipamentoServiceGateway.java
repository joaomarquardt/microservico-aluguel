package com.es2.microservicos.gateways;

import com.es2.microservicos.dtos.responses.BicicletaResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Service
public class EquipamentoServiceGateway {
    private final RestClient restClient;

    public EquipamentoServiceGateway(@Qualifier("restClientEquipamento") RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity<BicicletaResponse> obterBicicletaPorId(Long idCiclista) {
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<BicicletaResponse> obterBicicletaPorIdTranca(Long idTranca) {
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<BicicletaResponse> existeTrancaPorId(Long idTranca) {
        return ResponseEntity.ok().build();
    }
}

package com.es2.microservicos.external.gateways;

import com.es2.microservicos.dtos.responses.BicicletaResponse;
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

    public ResponseEntity<BicicletaResponse> obterBicicletaPorId(Long idCiclista) {
        // TODO: Implementar chamada ao microserviço Equipamento
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<BicicletaResponse> obterBicicletaPorIdTranca(Long idTranca) {
        // TODO: Implementar chamada ao microserviço Equipamento
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Boolean> existeTrancaPorId(Long idTranca) {
        // TODO: Implementar chamada ao microserviço Equipamento
        return ResponseEntity.ok().build();
    }

     public ResponseEntity alterarStatusBicicleta(Long idBicicleta, BicicletaStatus status) {
         // TODO: Implementar chamada ao microserviço Equipamento
        return ResponseEntity.ok().build();
     }

    public ResponseEntity alterarStatusTranca(Long idTranca, TrancaStatus status) {
        // TODO: Implementar chamada ao microserviço Equipamento
        return ResponseEntity.ok().build();
    }
}

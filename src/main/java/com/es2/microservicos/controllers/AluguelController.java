package com.es2.microservicos.controllers;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.dtos.requests.GerarAluguelRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.services.AluguelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aluguel")
public class AluguelController {
    private final AluguelService aluguelService;

    public AluguelController(AluguelService aluguelService) {
        this.aluguelService = aluguelService;
    }

    @GetMapping
    public ResponseEntity<AluguelResponse> gerarAluguel(@RequestBody GerarAluguelRequest request) {
        AluguelResponse aluguel = aluguelService.gerarAluguel(request);
        return new ResponseEntity<>(aluguel, HttpStatus.OK);
    }
}

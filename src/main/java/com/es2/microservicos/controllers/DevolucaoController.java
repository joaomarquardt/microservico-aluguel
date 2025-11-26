package com.es2.microservicos.controllers;

import com.es2.microservicos.dtos.requests.DevolucaoRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.services.DevolucaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/devolucao")
public class DevolucaoController {

    private final DevolucaoService devolucaoService;

    public DevolucaoController(DevolucaoService devolucaoService) {
        this.devolucaoService = devolucaoService;
    }

    @PostMapping
    public ResponseEntity<AluguelResponse> devolverBicicleta(@RequestBody DevolucaoRequest request) {
        AluguelResponse aluguel = devolucaoService.devolverBicicleta(request);
        return new ResponseEntity<>(aluguel, HttpStatus.OK);
    }
}

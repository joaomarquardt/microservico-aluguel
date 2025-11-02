package com.es2.microservicos.controllers;

import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.services.AluguelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aluguel")
public class AluguelController {
    private AluguelService aluguelService;

    public AluguelController(AluguelService aluguelService) {
        this.aluguelService = aluguelService;
    }

    @GetMapping
    public ResponseEntity<Aluguel> gerarAluguel() {
        Aluguel aluguel = aluguelService.gerarAluguel();
        return new ResponseEntity<>(aluguel, HttpStatus.OK);
    }
}

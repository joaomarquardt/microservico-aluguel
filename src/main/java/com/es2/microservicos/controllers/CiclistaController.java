package com.es2.microservicos.controllers;

import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.services.CiclistaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ciclista")
public class CiclistaController {
    private CiclistaService ciclistaService;

    public CiclistaController(CiclistaService ciclistaService) {
        this.ciclistaService = ciclistaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ciclista> obterCiclistaPorId(@PathVariable(value = "id") Long id) {
        Ciclista ciclista = ciclistaService.obterCiclistaPorId(id);
        return new ResponseEntity<>(ciclista, HttpStatus.OK);
    }
}

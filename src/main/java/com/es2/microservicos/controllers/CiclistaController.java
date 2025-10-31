package com.es2.microservicos.controllers;

import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.services.CiclistaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{id}")
    public ResponseEntity<Ciclista> atualizarCiclista(@PathVariable(value = "id") Long id, @RequestBody Ciclista ciclistaDetalhes) {
        Ciclista ciclistaAtualizado = ciclistaService.atualizarCiclista(id, ciclistaDetalhes);
        return new ResponseEntity<>(ciclistaAtualizado, HttpStatus.OK);
    }
}

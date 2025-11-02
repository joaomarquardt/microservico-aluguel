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

    @PostMapping("/{id}/ativar")
    public ResponseEntity<Ciclista> ativarCiclista(@PathVariable(value = "id") Long id) {
        Ciclista novoCiclista = ciclistaService.ativarCiclista(id);
        return new ResponseEntity<>(novoCiclista, HttpStatus.OK);
    }

    @GetMapping("/{id}/permiteAluguel")
    public ResponseEntity<Boolean> verificarPermissaoAluguel(@PathVariable(value = "id") Long id) {
        Boolean podeAlugar = ciclistaService.verificarPermissaoAluguel(id);
        return new ResponseEntity<>(podeAlugar, HttpStatus.OK);
    }

    @GetMapping("/{id}/bicicletaAlugada")
    public ResponseEntity<?> obterBicicletaAlugada(@PathVariable(value = "id") Long id) {
        // TODO: Alterar tipo de variável após implementar integração com microserviço de Equipamento e criação de DTOs
        var bicicletaAlugada = ciclistaService.obterBicicletaAlugada(id);
        return new ResponseEntity<>(bicicletaAlugada, HttpStatus.OK);
    }

    @GetMapping("/existeEmail/{email}")
    public ResponseEntity<Boolean> verificarExistenciaEmail(@PathVariable(value = "email") String email) {
        Boolean emailExiste = ciclistaService.verificarExistenciaEmail(email);
        return new ResponseEntity<>(emailExiste, HttpStatus.OK);
    }
}

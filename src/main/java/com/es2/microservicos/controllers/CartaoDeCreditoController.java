package com.es2.microservicos.controllers;

import com.es2.microservicos.domain.CartaoDeCredito;
import com.es2.microservicos.services.CartaoDeCreditoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cartaoDeCredito")
public class CartaoDeCreditoController {
    private CartaoDeCreditoService cartaoDeCreditoService;

    public CartaoDeCreditoController(CartaoDeCreditoService cartaoDeCreditoService) {
        this.cartaoDeCreditoService = cartaoDeCreditoService;
    }

    @GetMapping("/{idCiclista}")
    public ResponseEntity<CartaoDeCredito> obterCartaoDeCreditoPorIdCiclista(@PathVariable(value = "idCiclista") Long idCiclista) {
        CartaoDeCredito cartaoDeCredito = cartaoDeCreditoService.obterCartaoDeCreditoPorIdCiclista(idCiclista);
        return new ResponseEntity<>(cartaoDeCredito, HttpStatus.OK);
    }

    @PutMapping("/{idCiclista}")
    public ResponseEntity<CartaoDeCredito> atualizarCartaoDeCreditoPorIdCiclista(@PathVariable(value = "idCiclista") Long idCiclista, @RequestBody CartaoDeCredito cartaoDeCreditoDetalhes) {
        CartaoDeCredito cartaoDeCredito = cartaoDeCreditoService.atualizarCartaoDeCreditoPorIdCiclista(idCiclista);
        return new ResponseEntity<>(cartaoDeCredito, HttpStatus.OK);
    }
}

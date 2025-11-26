package com.es2.microservicos.controllers;

import com.es2.microservicos.services.RestaurarBancoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurarBanco")
public class RestaurarBancoController {
    private final RestaurarBancoService restaurarBancoService;

    public RestaurarBancoController(RestaurarBancoService restaurarBancoService) {
        this.restaurarBancoService = restaurarBancoService;
    }

    @GetMapping
    public ResponseEntity<HttpStatus> restaurarBanco() {
        restaurarBancoService.restaurarBanco();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

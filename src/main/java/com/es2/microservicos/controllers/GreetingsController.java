package com.es2.microservicos.controllers;

import com.es2.microservicos.services.GreetingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greetings")
public class GreetingsController {
    private final GreetingsService greetingsService;

    public GreetingsController(GreetingsService greetingsService) {
        this.greetingsService = greetingsService;
    }

    @GetMapping
    public ResponseEntity<String> getGreetings() {
        String greetings = greetingsService.getGreetings();
        return new ResponseEntity<>(greetings, HttpStatus.OK);
    }
}

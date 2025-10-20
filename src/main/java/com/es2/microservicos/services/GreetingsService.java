package com.es2.microservicos.services;

import org.springframework.stereotype.Service;

@Service
public class GreetingsService {

    public String getGreetings() {
        return "Hello World";
    }
}

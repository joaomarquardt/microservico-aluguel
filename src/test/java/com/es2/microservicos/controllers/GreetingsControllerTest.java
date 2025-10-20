package com.es2.microservicos.controllers;

import com.es2.microservicos.services.GreetingsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GreetingsControllerTest {

    @Mock
    private GreetingsService greetingsService;

    @InjectMocks
    private GreetingsController greetingsController;

    public GreetingsControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetGreetings() {
        when(greetingsService.getGreetings()).thenReturn("Hello World");
        ResponseEntity<String> response = greetingsController.getGreetings();
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("Hello World", response.getBody());
    }
}


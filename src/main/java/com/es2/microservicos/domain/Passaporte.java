package com.es2.microservicos.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "passaportes")
public class Passaporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numero;
    private LocalDate validade;
    private String pais;

    public Passaporte(String numero, LocalDate validade, String pais) {
        this.numero = numero;
        this.validade = validade;
        this.pais = pais;
    }
}

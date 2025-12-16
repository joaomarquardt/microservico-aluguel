package com.es2.microservicos.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "cartoes_de_credito")
public class CartaoDeCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomeTitular;
    private String numero;
    private LocalDate validade;
    private String cvv;
    @ManyToOne
    @JoinColumn(name = "ciclista_id")
    private Ciclista ciclista;

    public CartaoDeCredito() {
    }

    public CartaoDeCredito(Long id, String nomeTitular, String numero, LocalDate validade, String cvv, Ciclista ciclista) {
        this.id = id;
        this.nomeTitular = nomeTitular;
        this.numero = numero;
        this.validade = validade;
        this.cvv = cvv;
        this.ciclista = ciclista;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public void setNomeTitular(String nomeTitular) {
        this.nomeTitular = nomeTitular;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Ciclista getCiclista() {
        return ciclista;
    }

    public void setCiclista(Ciclista ciclista) {
        this.ciclista = ciclista;
    }
}

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
    private String numeroCartao;
    private LocalDate dataValidade;
    private String codigoSeguranca;

    public CartaoDeCredito() {
    }

    public CartaoDeCredito(Long id, String nomeTitular, String numeroCartao, LocalDate dataValidade, String codigoSeguranca) {
        this.id = id;
        this.nomeTitular = nomeTitular;
        this.numeroCartao = numeroCartao;
        this.dataValidade = dataValidade;
        this.codigoSeguranca = codigoSeguranca;
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

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getCodigoSeguranca() {
        return codigoSeguranca;
    }

    public void setCodigoSeguranca(String codigoSeguranca) {
        this.codigoSeguranca = codigoSeguranca;
    }
}

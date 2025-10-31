package com.es2.microservicos.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "ciclistas")
public class Ciclista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private Status status;
    private String cpf;
    private LocalDate nascimento;
    private Nacionalidade nacionalidade;
    private Passaporte passaporte;
    private String urlFotoDocumento;

    public Ciclista() {
    }

    public Ciclista(Long id, String nome, String email, Status status, String cpf, LocalDate nascimento, Nacionalidade nacionalidade, Passaporte passaporte, String urlFotoDocumento) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.status = status;
        this.cpf = cpf;
        this.nascimento = nascimento;
        this.nacionalidade = nacionalidade;
        this.passaporte = passaporte;
        this.urlFotoDocumento = urlFotoDocumento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getNascimento() {
        return nascimento;
    }

    public void setNascimento(LocalDate nascimento) {
        this.nascimento = nascimento;
    }

    public Nacionalidade getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(Nacionalidade nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public Passaporte getPassaporte() {
        return passaporte;
    }

    public void setPassaporte(Passaporte passaporte) {
        this.passaporte = passaporte;
    }

    public String getUrlFotoDocumento() {
        return urlFotoDocumento;
    }

    public void setUrlFotoDocumento(String urlFotoDocumento) {
        this.urlFotoDocumento = urlFotoDocumento;
    }
}

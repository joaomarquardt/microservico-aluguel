package com.es2.microservicos.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alugueis")
public class Aluguel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long bicicletaId;
    @ManyToOne
    @JoinColumn(name = "ciclista_id")
    private Ciclista ciclista;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFim;
    private Integer cobranca;
    private Long trancaInicio;
    private Long trancaFim = 0L;

    public Aluguel() {
    }

    public Aluguel(Long id, Long bicicletaId, Ciclista ciclista, LocalDateTime horaInicio, LocalDateTime horaFim, Integer cobranca, Long trancaInicio, Long trancaFim) {
        this.id = id;
        this.bicicletaId = bicicletaId;
        this.ciclista = ciclista;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.cobranca = cobranca;
        this.trancaInicio = trancaInicio;
        this.trancaFim = trancaFim;
    }

    public Aluguel(Long bicicletaId, Ciclista ciclista, LocalDateTime horaInicio, LocalDateTime horaFim, Integer cobranca, Long trancaInicio, Long trancaFim) {
        this.bicicletaId = bicicletaId;
        this.ciclista = ciclista;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.cobranca = cobranca;
        this.trancaInicio = trancaInicio;
        this.trancaFim = trancaFim;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBicicletaId() {
        return bicicletaId;
    }

    public void setBicicletaId(Long bicicletaId) {
        this.bicicletaId = bicicletaId;
    }

    public Ciclista getCiclista() {
        return ciclista;
    }

    public void setCiclista(Ciclista ciclista) {
        this.ciclista = ciclista;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalDateTime horaFim) {
        this.horaFim = horaFim;
    }

    public Integer getCobranca() {
        return cobranca;
    }

    public void setCobranca(Integer cobranca) {
        this.cobranca = cobranca;
    }

    public Long getTrancaInicio() {
        return trancaInicio;
    }

    public void setTrancaInicio(Long trancaInicio) {
        this.trancaInicio = trancaInicio;
    }

    public Long getTrancaFim() {
        return trancaFim;
    }

    public void setTrancaFim(Long trancaFim) {
        this.trancaFim = trancaFim;
    }
}

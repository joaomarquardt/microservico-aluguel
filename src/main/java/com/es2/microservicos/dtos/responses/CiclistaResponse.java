package com.es2.microservicos.dtos.responses;

import com.es2.microservicos.domain.Nacionalidade;
import com.es2.microservicos.domain.Status;

import java.time.LocalDate;

public record CiclistaResponse(
        Long id,
        Status status,
        String nome,
        LocalDate nascimento,
        String cpf,
        PassaporteResponse passaporte,
        Nacionalidade nacionalidade,
        String email,
        String urlFotoDocumento
) {
}

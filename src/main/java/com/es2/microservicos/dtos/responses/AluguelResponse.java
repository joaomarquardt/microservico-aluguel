package com.es2.microservicos.dtos.responses;

import java.time.LocalDateTime;

public record AluguelResponse(
        Long bicicletaId,
        LocalDateTime horaInicio,
        Long trancaInicio,
        LocalDateTime horaFim,
        Long trancaFim,
        Integer cobranca,
        Long ciclistaId
) {
}

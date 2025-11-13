package com.es2.microservicos.mappers;

import com.es2.microservicos.domain.Ciclista;
import com.es2.microservicos.dtos.requests.AtualizarCiclistaRequest;
import com.es2.microservicos.dtos.requests.CriarCiclistaRequest;
import com.es2.microservicos.dtos.responses.CiclistaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CiclistaMapper {
    CiclistaMapper INSTANCE = Mappers.getMapper(CiclistaMapper.class);

    CiclistaResponse toCiclistaResponse(Ciclista ciclista);

    Ciclista toCiclista(CriarCiclistaRequest criarCiclistaRequest);

    List<CiclistaResponse> toCiclistaResponseList(List<Ciclista> ciclistas);

    @Mapping(target = "id", ignore = true)
    void updateCiclistaFromRequest(AtualizarCiclistaRequest request, Ciclista ciclista);
}

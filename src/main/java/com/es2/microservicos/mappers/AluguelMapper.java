package com.es2.microservicos.mappers;


import com.es2.microservicos.domain.Aluguel;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AluguelMapper {
    AluguelMapper INSTANCE = Mappers.getMapper(AluguelMapper.class);

    AluguelResponse toAluguelResponse(Aluguel aluguel);
}

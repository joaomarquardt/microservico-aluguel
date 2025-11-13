package com.es2.microservicos.mappers;

import com.es2.microservicos.domain.CartaoDeCredito;
import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.responses.CartaoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CartaoDeCreditoMapper {
    CartaoDeCreditoMapper ISTANCE = Mappers.getMapper(CartaoDeCreditoMapper.class);

    CartaoDeCredito toCartaoDeCredito(AdicionarCartaoRequest adicionarCartaoRequest);

    CartaoResponse toCartaoResponse(CartaoDeCredito cartaoDeCredito);

    @Mapping(target = "id", ignore = true)
    void updateCartaoDeCreditoFromRequest(AdicionarCartaoRequest request, CartaoDeCredito cartaoDeCredito);
}

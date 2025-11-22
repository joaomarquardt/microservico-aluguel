package com.es2.microservicos.mappers;

import com.es2.microservicos.domain.Funcionario;
import com.es2.microservicos.dtos.requests.AtualizarFuncionarioRequest;
import com.es2.microservicos.dtos.requests.CriarFuncionarioRequest;
import com.es2.microservicos.dtos.responses.FuncionarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FuncionarioMapper {
    FuncionarioMapper INSTANCE = Mappers.getMapper(FuncionarioMapper.class);

    Funcionario toFuncionario(CriarFuncionarioRequest criarFuncionarioRequest);

    FuncionarioResponse toFuncionarioResponse(Funcionario funcionario);

    List<FuncionarioResponse> toFuncionarioResponseList(List<Funcionario> funcionarios);

    @Mapping(target = "id", ignore = true)
    void updateFuncionarioFromRequest(AtualizarFuncionarioRequest request, @MappingTarget Funcionario funcionario);
}

package com.es2.microservicos.services;

import com.es2.microservicos.domain.Funcionario;
import com.es2.microservicos.dtos.requests.AtualizarFuncionarioRequest;
import com.es2.microservicos.dtos.requests.CriarFuncionarioRequest;
import com.es2.microservicos.dtos.responses.FuncionarioResponse;
import com.es2.microservicos.mappers.FuncionarioMapper;
import com.es2.microservicos.repositories.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FuncionarioService {
    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioMapper funcionarioMapper;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, FuncionarioMapper funcionarioMapper) {
        this.funcionarioMapper = funcionarioMapper;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<FuncionarioResponse> listarFuncionarios() {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        return funcionarioMapper.toFuncionarioResponseList(funcionarios);
    }

    public FuncionarioResponse obterFuncionarioPorId(Long id) {
        Funcionario funcionario = obterEntidadeFuncionarioPorId(id);
        return funcionarioMapper.toFuncionarioResponse(funcionario);
    }

    public Funcionario obterEntidadeFuncionarioPorId(Long id) {
        return funcionarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado!"));
    }

    public FuncionarioResponse criarFuncionario(CriarFuncionarioRequest funcionarioRequest) {
        if (!funcionarioRequest.senha().equals(funcionarioRequest.confirmacaoSenha())) {
            throw new IllegalArgumentException("Senha e confirmação de senha não coincidem!");
        }
        Funcionario funcionario = funcionarioMapper.toFuncionario(funcionarioRequest);
        funcionario.setMatricula(gerarMatricula());
        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
        return funcionarioMapper.toFuncionarioResponse(funcionarioSalvo);
    }

    public FuncionarioResponse atualizarFuncionario(Long id, AtualizarFuncionarioRequest funcionarioRequest) {
        if (!funcionarioRequest.senha().equals(funcionarioRequest.confirmacaoSenha())) {
            throw new IllegalArgumentException("Senha e confirmação de senha não coincidem!");
        }
        Funcionario funcionarioExistente = obterEntidadeFuncionarioPorId(id);
        funcionarioMapper.updateFuncionarioFromRequest(funcionarioRequest, funcionarioExistente);
        Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionarioExistente);
        return funcionarioMapper.toFuncionarioResponse(funcionarioAtualizado);
    }

    public void deletarFuncionario(Long id) {
        Funcionario funcionarioExistente = obterEntidadeFuncionarioPorId(id);
        funcionarioRepository.delete(funcionarioExistente);
    }

    private String gerarMatricula() {
        String prefixo = "FUNC";
        long contador = funcionarioRepository.count() + 1;
        return prefixo + String.format("%05d", contador);
    }
}

package com.es2.microservicos.services;

import com.es2.microservicos.domain.Funcionario;
import com.es2.microservicos.repositories.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioService {
    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepository.findAll();
    }

    public Funcionario obterFuncionarioPorId(Long id) {
        return funcionarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado!"));
    }

    public Funcionario criarFuncionario(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    public Funcionario atualizarFuncionario(Long id, Funcionario funcionarioDetalhes) {
        Funcionario funcionarioExistente = funcionarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado!"));
        funcionarioExistente.setMatricula(funcionarioDetalhes.getMatricula());
        funcionarioExistente.setNome(funcionarioDetalhes.getNome());
        funcionarioExistente.setEmail(funcionarioDetalhes.getEmail());
        funcionarioExistente.setSenha(funcionarioDetalhes.getSenha());
        funcionarioExistente.setConfirmacaoSenha(funcionarioDetalhes.getConfirmacaoSenha());
        funcionarioExistente.setCpf(funcionarioDetalhes.getCpf());
        funcionarioExistente.setIdade(funcionarioDetalhes.getIdade());
        funcionarioExistente.setFuncao(funcionarioDetalhes.getFuncao());
        return funcionarioRepository.save(funcionarioExistente);
    }

    public void deletarFuncionario(Long id) {
        Funcionario funcionarioExistente = funcionarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado!"));
        funcionarioRepository.delete(funcionarioExistente);
    }
}

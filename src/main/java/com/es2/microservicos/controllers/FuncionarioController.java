package com.es2.microservicos.controllers;

import com.es2.microservicos.domain.Funcionario;
import com.es2.microservicos.dtos.requests.AtualizarFuncionarioRequest;
import com.es2.microservicos.dtos.requests.CriarFuncionarioRequest;
import com.es2.microservicos.dtos.responses.FuncionarioResponse;
import com.es2.microservicos.services.FuncionarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {
    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponse>> listarFuncionarios() {
        List<FuncionarioResponse> funcionarios = funcionarioService.listarFuncionarios();
        return new ResponseEntity<>(funcionarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> obterFuncionarioPorId(@PathVariable(value = "id") Long id) {
        FuncionarioResponse funcionario = funcionarioService.obterFuncionarioPorId(id);
        return new ResponseEntity<>(funcionario, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponse> criarFuncionario(@RequestBody CriarFuncionarioRequest funcionario) {
        FuncionarioResponse novoFuncionario = funcionarioService.criarFuncionario(funcionario);
        return new ResponseEntity<>(novoFuncionario, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> atualizarFuncionario(@PathVariable(value = "id") Long id, @RequestBody AtualizarFuncionarioRequest funcionarioDetalhes) {
        FuncionarioResponse funcionarioAtualizado = funcionarioService.atualizarFuncionario(id, funcionarioDetalhes);
        return new ResponseEntity<>(funcionarioAtualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable(value = "id") Long id) {
        funcionarioService.deletarFuncionario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

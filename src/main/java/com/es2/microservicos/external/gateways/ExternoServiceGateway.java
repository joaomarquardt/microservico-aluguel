package com.es2.microservicos.external.gateways;

import com.es2.microservicos.dtos.requests.AdicionarCartaoRequest;
import com.es2.microservicos.dtos.requests.CobrancaRequest;
import com.es2.microservicos.dtos.requests.EmailRequest;
import com.es2.microservicos.dtos.responses.AluguelResponse;
import com.es2.microservicos.dtos.responses.CobrancaResponse;
import com.es2.microservicos.dtos.responses.EmailResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternoServiceGateway {
    private final RestClient restClient;

    public ExternoServiceGateway(@Qualifier("restClientExterno") RestClient restClient) {
        this.restClient = restClient;
    }

    private void enviarEmail(EmailRequest emailDetalhes) {
        ResponseEntity<EmailResponse> emailResponse = restClient.post()
                .uri("/enviarEmail")
                .body(emailDetalhes)
                .retrieve()
                .toEntity(EmailResponse.class);
        if (!emailResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("Falha ao enviar email com assunto '" + emailDetalhes.assunto() + "' para: " + emailDetalhes.emailDestinatario());
        }
    }

    public void confirmacaoCadastroEmail(String emailDestinatario) {
        String assunto = "Confirmação de Cadastro";
        String corpo = "Seu cadastro foi realizado com sucesso!";
        EmailRequest emailRequest = new EmailRequest(emailDestinatario, assunto, corpo);
        enviarEmail(emailRequest);
    }

    public void atualizacaoCiclistaEmail(String emailDestinatario) {
        String assunto = "Atualização de Dados do Ciclista";
        String corpo = "Seus dados foram atualizados com sucesso!";
        EmailRequest emailRequest = new EmailRequest(emailDestinatario, assunto, corpo);
        enviarEmail(emailRequest);
    }

    public void atualizacaoCartaoEmail(String emailDestinatario) {
        String assunto = "Atualização de Dados do Cartão de Crédito";
        String corpo = "Os dados do seu cartão de crédito foram atualizados com sucesso!";
        EmailRequest emailRequest = new EmailRequest(emailDestinatario, assunto, corpo);
        enviarEmail(emailRequest);
    }

    public void dadosAluguelAtualEmail(String emailDestinatario, AluguelResponse detalhesAluguel) {
        String assunto = "Aluguel em Andamento";
        String corpo = "Você já possui um aluguel em andamento e, portanto, não é possível realizar outro aluguel. Detalhes do aluguel: " + detalhesAluguel.toString();
        EmailRequest emailRequest = new EmailRequest(emailDestinatario, assunto, corpo);
        enviarEmail(emailRequest);
    }

    public void dadosAluguelNovoEmail(String emailDestinatario, AluguelResponse detalhesAluguel) {
        String assunto = "Confirmação do Novo Aluguel";
        String corpo = "Seu novo aluguel foi iniciado com sucesso. Detalhes do aluguel: " + detalhesAluguel.toString();
        EmailRequest emailRequest = new EmailRequest(emailDestinatario, assunto, corpo);
        enviarEmail(emailRequest);
    }

    public void devolucaoBicicletaEmail(String nomeDestinatario, String emailDestinatario, AluguelResponse aluguelResponse, double taxaExtra) {
        String assunto = "Confirmação de Devolução da Bicicleta";
        String corpo = "Olá " + nomeDestinatario + ",\n\nSua bicicleta foi devolvida com sucesso.\nDetalhes do aluguel: " + aluguelResponse.toString() +
                "\nTaxa extra cobrada: R$ " + taxaExtra + "\n\nObrigado por utilizar nosso serviço!";
        EmailRequest emailRequest = new EmailRequest(emailDestinatario, assunto, corpo);
        enviarEmail(emailRequest);
    }

    public CobrancaResponse cobrarAluguel(double valor, Long idCiclista) {
        CobrancaRequest cobrancaRequest = new CobrancaRequest(valor, idCiclista);
        ResponseEntity<CobrancaResponse> cobrancaResponse = restClient.post()
                .uri("/cobranca")
                .body(cobrancaRequest)
                .retrieve()
                .toEntity(CobrancaResponse.class);
        if (!cobrancaResponse.getStatusCode().is2xxSuccessful() || !cobrancaResponse.hasBody()) {
            throw new IllegalArgumentException("Falha ao processar a cobrança do aluguel!");
        }
        return cobrancaResponse.getBody();
    }

    public void validacaoCartaoDeCredito(AdicionarCartaoRequest cartaoRequest) {
        ResponseEntity<Void> validacaoResponse = restClient.post()
                .uri("/validaCartaoDeCredito")
                .body(cartaoRequest)
                .retrieve()
                .toEntity(Void.class);
        if (!validacaoResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("Validação do cartão de crédito falhou!");
        }
    }
}

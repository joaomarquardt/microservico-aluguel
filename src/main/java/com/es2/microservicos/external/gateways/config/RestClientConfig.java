package com.es2.microservicos.external.gateways.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClientExterno(@Value("${externo.service.url}") String externoServiceUrl)  {
        return RestClient.builder()
                .baseUrl(externoServiceUrl)
                .build();
    }

    @Bean
    public RestClient restClientEquipamento(@Value("${equipamento.service.url}") String equipamentoServiceUrl)  {
        return RestClient.builder()
                .baseUrl(equipamentoServiceUrl)
                .build();
    }

}

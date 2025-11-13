package com.es2.microservicos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClientExterno()  {
        return RestClient.builder()
                .baseUrl("http://externo-microservico/api")  // TODO: Definir a URL base do microserviço Externo
                .build();
    }

    @Bean
    public RestClient restClientEquipamento()  {
        return RestClient.builder()
                .baseUrl("http://equipamento-microservico/api")  // TODO: Definir a URL base do microserviço Equipamento
                .build();
    }

}

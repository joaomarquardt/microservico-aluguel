# Microserviço de Aluguel - Sistema de Controle de Bicicletário

Este microserviço foi desenvolvido para a disciplina de **Engenharia de Software 2**. A aplicação é responsável por gerir o ciclo de vida dos ciclistas e as operações de locação e devolução de bicicletas, integrando-se com os serviços de equipamentos e serviços externos.

## Links do Projeto

* **Hospedagem:** [Render](https://microservicos-2vnr.onrender.com/).
* **Qualidade de Código:** Integrado ao [SonarCloud](https://sonarcloud.io/summary/new_code?id=joaomarquardt_microservicos&branch=main), com +90% de coverage e 0 code smells críticos.
* **Especificação de Requisitos:** [Documento](https://docs.google.com/document/d/1oyHha7fzeXCo8jG_eR2us7uqqXAKFYKjXb2ce3Tlbvs/edit?tab=t.0).
* **Swagger API:** [Link](https://app.swaggerhub.com/apis/pasemes/sistema-de_controle_de_bicicletario2/1).
---

## Tecnologias Utilizadas

* **Java 21** e **Spring Boot 3.5**.
* **Spring Data JPA**
* **Spring Validation**
* **MapStruct** 
* **RestClient**
* **JUnit 5 & Mockito**
* **Docker**

---

## Integrações (Ecossistema)

Este ecossistema é composto por três microserviços independentes. Pode consultar os outros módulos aqui:

* [**Microserviço de Equipamento**](https://github.com/juliaaleoni/equipment-service-es2): – Responsável pela gestão física de bicicletas, trancas e totens.
* [**Microserviço Externo**](https://github.com/gsalviete/external-service): – Gere o envio de e-mails, validação de cartões e processamento de pagamentos.

---

## ⚙️ Como Executar


### Executar Localmente
É necessário preencher as seguintes informações no application.properties para poder executar localmente:
```bash
DATABASE_URL=jdbc:mysql://localhost:3306/<nome do seu banco>
DATABASE_USERNAME<seu usuario>
DATABASE_PASSWORD=<sua senha>
EXTERNAL_MICROSERVICE_URL=<URL do microserviço externo rodando na sua máquina>
EQUIPAMENT_MICROSERVICE_URL=<URL do microserviço externo rodando na sua máquina>
```

Após preenchimento das informações no application.properties, rode o comando de incialização do projeto:
```bash
./mvnw spring-boot:run
```

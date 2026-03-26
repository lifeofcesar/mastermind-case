# 🎮 Mastermind Web Game

> Case técnico desenvolvido para o processo seletivo do **Itaú Unibanco**.
> Versão web do clássico jogo de lógica **Mastermind**, com backend em Java/Spring Boot e frontend em Angular.

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Como o Jogo Funciona](#-como-o-jogo-funciona)
- [Stack Tecnológica](#-stack-tecnológica)
- [Estrutura do Repositório](#-estrutura-do-repositório)
- [Pré-requisitos](#-pré-requisitos)
- [Como Executar](#-como-executar)
  - [1. Clonar o Repositório](#1-clonar-o-repositório)
  - [2. Subir o Banco de Dados](#2-subir-o-banco-de-dados)
  - [3. Executar o Backend](#3-executar-o-backend)
  - [4. Executar o Frontend](#4-executar-o-frontend)
- [Configurações do Banco de Dados](#️-configurações-do-banco-de-dados)
- [Documentação da API (Swagger)](#-documentação-da-api-swagger)
- [Rodando os Testes Unitários](#-rodando-os-testes-unitários)
- [Acessando a Aplicação](#-acessando-a-aplicação)

---

## 📖 Sobre o Projeto

O **Mastermind Web Game** é uma versão web do clássico jogo de lógica e dedução **Mastermind**, desenvolvido como case técnico para o processo seletivo do **Itaú Unibanco**.

A solução é composta por uma **API RESTful** em Java/Spring Boot e uma **interface web** em Angular, com autenticação de usuários, persistência de partidas e ranking de jogadores.

---

## 🧠 Como o Jogo Funciona

O Mastermind é um jogo de lógica em que o objetivo é descobrir uma **combinação secreta de cores** gerada pelo sistema, dentro de um número limitado de tentativas.

**Fluxo de uma partida:**

1. O jogador faz login e inicia uma nova partida.
2. O backend gera uma combinação secreta (nunca exposta ao frontend) e cria um registro único da partida.
3. O jogador monta sua tentativa escolhendo uma sequência de cores/valores no tabuleiro e a submete.
4. O backend valida a tentativa e retorna apenas **quantas posições estão corretas** — sem revelar quais são.
5. O jogador usa esse feedback para refinar as próximas tentativas.
6. A partida termina quando o jogador acerta a combinação ou esgota as **10 tentativas** disponíveis.
7. O resultado é salvo com pontuação, tempo de duração e histórico completo de tentativas.

**Telas da aplicação:**

| Tela | Descrição |
|------|-----------|
| Login | Autenticação com e-mail/usuário e senha |
| Dashboard | Menu principal para iniciar partida ou ver ranking |
| Jogo | Tabuleiro interativo com matriz de tentativas e feedback visual |
| Ranking | Classificação dos jogadores ordenada por desempenho |



## 🛠 Stack Tecnológica

| Camada     | Tecnologia                          |
|------------|-------------------------------------|
| Backend    | Java 17+, Spring Boot, Maven        |
| Banco de Dados | PostgreSQL 15                   |
| Frontend   | Angular 15+, TypeScript, Node.js    |
| Infraestrutura | Docker, Docker Compose          |

---

## 📁 Estrutura do Repositório

```
mastermind-case/
├── backend/            # API RESTful (Spring Boot)
├── frontend/           # Aplicação Web (Angular)
├── docker-compose.yml  # Configuração do banco de dados (PostgreSQL)
├── .gitignore
└── README.md
```

---

## ✅ Pré-requisitos

Certifique-se de ter instalado na sua máquina:

- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/)
- [Java 17+](https://adoptium.net/) e [Maven](https://maven.apache.org/)
- [Node.js](https://nodejs.org/) (versão compatível com Angular 15+) e [npm](https://www.npmjs.com/)
- [Angular CLI](https://angular.io/cli) (`npm install -g @angular/cli`)

---

## 🚀 Como Executar

### 1. Clonar o Repositório

```bash
git clone https://github.com/lifeofcesar/mastermind-case.git
cd mastermind-case
```

---

### 2. Subir o Banco de Dados

O projeto utiliza **PostgreSQL via Docker**. Na raiz do repositório, execute:

```bash
docker-compose up -d
```

Isso irá iniciar o container `mastermind-db` com as seguintes configurações:

| Parâmetro | Valor              |
|-----------|--------------------|
| Host      | `localhost`        |
| Porta     | `5432`             |
| Banco     | `mastermind_db`    |
| Usuário   | `mastermind_user`  |
| Senha     | `mastermind_password` |

Para verificar se o container está rodando:

```bash
docker ps
```

---

### 3. Executar o Backend

Acesse a pasta do backend e rode a aplicação com Maven:

```bash
cd backend
mvn spring-boot:run
```

> A API ficará disponível em: **`http://localhost:8080`**

---

### 4. Executar o Frontend

Em um **novo terminal**, acesse a pasta do frontend e instale as dependências:

```bash
cd frontend
npm install
ng serve
```

> A aplicação ficará disponível em: **`http://localhost:4200`**

---

## 🗄️ Configurações do Banco de Dados

As configurações do backend ficam no arquivo `application.yml`, que já está alinhado com as credenciais do `docker-compose.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mastermind_db
    username: mastermind_user
    password: mastermind_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

logging:
  level:
    org.springframework.security: TRACE
    com.mastermind: DEBUG

server:
  port: 8080
```

Se precisar ajustar alguma configuração, edite o arquivo:

```
backend/src/main/resources/application.yml
```

---

## 📚 Documentação da API (Swagger)

A API possui documentação interativa gerada automaticamente pelo **Springdoc OpenAPI**, cumprindo o requisito de documentação mínima da API.

Com o backend em execução, acesse a URL abaixo no seu navegador para explorar e testar todos os endpoints de forma visual:

🔗 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Lá você encontrará todos os endpoints disponíveis, como `/auth/login`, `/matches/start`, entre outros, com descrição de parâmetros e exemplos de resposta.

---

## 🧪 Rodando os Testes Unitários

O projeto conta com suítes de testes para garantir a integridade da aplicação, conforme exigido nos requisitos não funcionais.

### Backend (Regras de Negócio)

Testa a lógica central do Mastermind — validação de cores, cálculo de acertos e controle de tentativas — utilizando **JUnit 5** e **Mockito**:

```bash
cd backend
mvn test
```

### Frontend (Componentes Visuais)

Testa a renderização e o comportamento dos componentes Angular:

```bash
cd frontend
ng test
```

---

## 🌐 Acessando a Aplicação

| Serviço           | URL                                              |
|-------------------|--------------------------------------------------|
| Frontend          | http://localhost:4200                            |
| Backend (API)     | http://localhost:8080                            |
| Swagger UI        | http://localhost:8080/swagger-ui/index.html      |
| PostgreSQL        | localhost:5432                                   |

---

## Pagina de login:
<img width="1919" height="946" alt="image" src="https://github.com/user-attachments/assets/1c99db3f-f218-473d-a75f-c4219ce6e828" />

## Dashboard Inicial:
<img width="1919" height="1020" alt="image" src="https://github.com/user-attachments/assets/f77d6ad0-36aa-412e-8453-6c4198fbbbcc" />

## Pagina de jogo:
<img width="1919" height="828" alt="image" src="https://github.com/user-attachments/assets/7cc005a2-9e00-42d8-8f99-6c1362b5e75e" />



## 🔗 Repositório

[https://github.com/lifeofcesar/mastermind-case](https://github.com/lifeofcesar/mastermind-case)

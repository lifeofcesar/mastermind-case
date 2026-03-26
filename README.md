# 🎮 Mastermind Web Game

> Case técnico desenvolvido para o processo seletivo do **Itaú Unibanco**.
> Versão web do clássico jogo de lógica **Mastermind**, com backend em Java/Spring Boot e frontend em Angular.

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Stack Tecnológica](#-stack-tecnológica)
- [Estrutura do Repositório](#-estrutura-do-repositório)
- [Pré-requisitos](#-pré-requisitos)
- [Como Executar](#-como-executar)
  - [1. Clonar o Repositório](#1-clonar-o-repositório)
  - [2. Subir o Banco de Dados](#2-subir-o-banco-de-dados)
  - [3. Executar o Backend](#3-executar-o-backend)
  - [4. Executar o Frontend](#4-executar-o-frontend)
- [Configurações do Banco de Dados](#️-configurações-do-banco-de-dados)
- [Acessando a Aplicação](#-acessando-a-aplicação)

---

## 📖 Sobre o Projeto

O **Mastermind** é um jogo clássico de lógica e dedução. O objetivo é descobrir uma sequência secreta de cores (ou números) dentro de um número limitado de tentativas, recebendo dicas a cada palpite sobre quantos elementos estão corretos na posição certa e quantos estão corretos mas fora de posição.

Este projeto implementa o jogo em formato web, com uma API RESTful no backend e uma interface interativa no frontend.

---

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

## 🌐 Acessando a Aplicação

| Serviço    | URL                          |
|------------|------------------------------|
| Frontend   | http://localhost:4200        |
| Backend (API) | http://localhost:8080     |
| PostgreSQL | localhost:5432               |

---

## 🔗 Repositório

[https://github.com/lifeofcesar/mastermind-case](https://github.com/lifeofcesar/mastermind-case)
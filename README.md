# RecycleMetrics API

RecycleMetrics API é o backend do projeto RecycleMetrics, uma aplicação voltada para o acompanhamento de compras, descartes de resíduos e apoio à mudança de hábitos sustentáveis.

Este repositório contém apenas a API REST desenvolvida em Java com Spring Boot.
O frontend (React + TypeScript) consome estes endpoints e é mantido em um repositório separado.

## Visão geral

A EcoTrack API expõe uma API REST responsável por:

- cadastro e autenticação de usuários;

- registro e consulta de compras;

- registro e consulta de descartes;

- cálculo de métricas de sustentabilidade (itens descartados, materiais mais recorrentes, destinos, porcentagem de reciclagem etc.);

- integração com um assistente virtual baseado em IA para tirar dúvidas sobre sustentabilidade.

## Principais tecnologias:

- **Java 21**

- **Spring Boot**

- **Spring Data JPA**

- **Spring Security + JWT**

- **PostgreSQL**

- **Spring AI + OpenAI API**

- **Testes com JUnit 5 e Mockito**

- **Docker e Docker Compose**

## Como executar localmente

### Pré-requisitos

- JDK 21 instalado e configurado (JAVA_HOME)

- Git

- Docker

- Chave de API da OpenAI (opcional, para o assistente virtual)

### Clonar o repositório

No terminal:

```bash
git clone https://github.com/MiguellAlvess/ecotrack-api.git
```

Entre no diretório

```bash
cd ecotrack-api
```

### Subir o banco de dados com Docker

O projeto já possui um arquivo docker-compose.yml configurado com dois bancos PostgreSQL:

`recycle-metrics` — usado em desenvolvimento

`recycle-metrics-test` — usado nos testes E2E

Para subir os serviços:

```bash
docker-compose up -d
```

#### Isso irá:

- baixar a imagem oficial do PostgreSQL (se necessário);

- criar os containers recycle-metrics-postgres (porta 5432) e recycle-metrics-postgres-test (porta 5433);

- disponibilizar os bancos para uso da aplicação.

Para verificar se os containers estão ativos:

```bash
docker ps
```

### Configurar a chave da OpenAI (opcional)

Caso deseje utilizar o assistente virtual, defina a variável de ambiente `OPENAI_API_KEY`.

Exemplos:

Linux/macOS:

```bash
export OPENAI_API_KEY="sua-chave-aqui"
```

Windows PowerShell:

```bash
$env:OPENAI_API_KEY="sua-chave-aqui"
```

#### Observação:

Sem essa variável, a aplicação sobe normalmente, mas as chamadas relacionadas ao assistente virtual irão falhar.

### Executar a aplicação

Na raiz do projeto:

```bash
mvn spring-boot:run
```

Por padrão, a aplicação ficará disponível em:

`http://localhost:8080`

## Documentação da API (Swagger)

A documentação interativa gerada pelo Springdoc OpenAPI pode ser acessada em:

`http://localhost:8080/swagger-ui.html`

## Diagrama de entidades

![Diagrama de Entidades](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/MiguellAlvess/ecotrack-api/refs/heads/main/docs/diagram.puml&v=1
)

## Arquitetura geral

O backend foi desenvolvido utilizando **Java com Spring Boot**, seguindo uma arquitetura em camadas que garante organização, escalabilidade e fácil manutenção.

As principais camadas são:

- **Controller:** responsável por receber as requisições HTTP, validar dados de entrada e retornar respostas ao cliente;
- **Domain:** responsável por representar as entidades do sistema
- **Service:** responsável pela aplicação das regras de negócio do sistema;
- **Repository:** responsável pela comunicação com o banco de dados por meio do Spring Data JPA;

#### Exemplo de um fluxo de atualização de usuário

<img width="901" height="583" alt="image" src="https://github.com/user-attachments/assets/aae0408a-1c9c-43ae-bd09-0dcfdf73a9a7" />

## Autenticação

A autenticação do projeto é baseada em JWT (JSON Web Token).

No sistema, todas as rotas são protegidas por autenticação, com exceção apenas das rotas de:

- Cadastro de usuário
- Login

Tanto no login quanto na criação de conta, o usuário já é automaticamente autenticado e recebe o token de acesso.

#### Exemplo do fluxo de autenticação

<img width="1050" height="708" alt="image" src="https://github.com/user-attachments/assets/dacf2cb3-da5c-416d-b72a-b61fa9ab90ef" />

## Visão geral dos endpoints

| Método                  | Endpoint            | Descrição                                         |
| :---------------------- | :------------------ | :------------------------------------------------ |
| _Autenticação_          |                     |                                                   |
| POST                    | /auth/login         | Autentica um usuário e retorna um token JWT.      |
| POST                    | /auth/register      | Registra um novo usuário.                         |
| _Descartes (Disposals)_ |                     |                                                   |
| POST                    | /api/disposals      | Registra um novo descarte para o usuário logado.  |
| GET                     | /api/disposals      | Retorna todos os descartes do usuário logado.     |
| GET                     | /api/disposals/{id} | Retorna um descarte específico do usuário logado. |
| DELETE                  | /api/disposals/{id} | Deleta um descarte do usuário logado.             |
| _Compras (Purchases)_   |                     |                                                   |
| POST                    | /api/purchases      | Registra uma nova compra para o usuário logado.   |
| GET                     | /api/purchases      | Retorna todas as compras do usuário logado.       |
| GET                     | /api/purchases/{id} | Retorna uma compra específica do usuário logado.  |
| DELETE                  | /api/purchases/{id} | Deleta uma compra do usuário logado.              |

## Testes

O projeto conta com:

**Testes unitários**
Principalmente na camada de service, com uso de Mockito para simular repositórios, mappers e serviços auxiliares.

**Testes de integração**
Controllers testados com MockMvc, validando status HTTP, payloads e regras de acesso;
Repositórios testados com @DataJpaTest, utilizando TestEntityManager para validar operações de CRUD e restrições.

**Testes de ponta a ponta (E2E)**
Foi adicionado um teste E2E de exemplo, simulando a criação do usuário em um ambiente real. Para isso foi utilizado:

- TestRestTemplate para chamar a API real;
- Persistência de dados em um banco PostgreSQL exclusivo de teste (recycle-metrics-test)

#### Como executar os testes

Com o banco de testes em execução (container recycle-metrics-postgres-test):

```bash
mvn test
```

## Documentação

Para acessar a documentação completa do projeto, pode acessar nossa Wiki:
https://github.com/MiguellAlvess/ecotrack-api/wiki

## Equipe

<table>
  <tr>
    <th>Miguel Alves</th>
    <th>João Vitor</th>
    <th>Daniela Vescia</th>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/d563577d-c61c-4192-b3bb-4416843fa85c" width="180" height="180" style="object-fit: cover;"><br>
      <a href="https://www.linkedin.com/in/miguel-alvess/">LinkedIn ➜</a>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/c860a46e-3cf0-4ec4-bf08-0da7c8c125db" width="180" height="180" style="object-fit: cover;"><br>
      <a href="https://www.linkedin.com/in/jo%C3%A3o-monteiro-8411aa309/">LinkedIn ➜</a>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/9240cb75-ed6e-4d6d-883a-d261ffac69fb" width="180" height="180" style="object-fit: cover;"><br>
      <a href="https://www.linkedin.com/in/daniela-vescia-732144102/">LinkedIn ➜</a>
    </td>
  </tr>
</table>

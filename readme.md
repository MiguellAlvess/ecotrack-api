# EcoTrack API

API RESTful para o projeto **EcoTrack**, uma plataforma para rastreamento de atividades de reciclagem de usuários.

Este projeto serve como o backend para a aplicação EcoTrack, fornecendo endpoints para gerenciar usuários, materiais recicláveis, compras e descartes de forma segura e eficiente.

## 🚀 Rodando o projeto localmente

### Pré-requisitos
- Java 21
- Maven 3.11.0

### Passos
1.  Clone o repositório:
    ```sh
    git clone https://github.com/MiguellAlvess/ecotrack-api.git
    ```
2.  Navegue até a pasta do projeto:
    ```sh
    cd ecotrack-api
    ```
3.  Execute a aplicação com o Maven:
    ```sh
    ./mvn spring-boot:run
    ```
A aplicação estará disponível em `http://localhost:8080`.

## 📖 Documentação da API (OpenAPI/Swagger )

A documentação interativa da API, gerada com Swagger, está disponível para consulta e teste dos endpoints. Após iniciar a aplicação, acesse os seguintes links:

-   **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html )
-   **Definição OpenAPI (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs )

## 📌 Visão Geral dos Endpoints

| Método | Endpoint                  | Descrição                                         |
| :----- | :------------------------ | :------------------------------------------------ |
| **Autenticação** |                           |                                                   |
| `POST` | `/auth/login`             | Autentica um usuário e retorna um token JWT.      |
| `POST` | `/auth/register`          | Registra um novo usuário.                         |
| **Materiais**    |                           |                                                   |
| `GET`  | `/api/materials`          | Retorna a lista de todos os tipos de materiais.   |
| `GET`  | `/api/materials/{id}`     | Retorna um material específico pelo seu ID.       |
| **Descartes (Disposals)** |                   |                                                   |
| `POST` | `/api/disposals`          | Registra um novo descarte para o usuário logado.  |
| `GET`  | `/api/disposals`          | Retorna todos os descartes do usuário logado.    |
| `GET`  | `/api/disposals/{id}`     | Retorna um descarte específico do usuário logado. |
| `DELETE`| `/api/disposals/{id}`    | Deleta um descarte do usuário logado.             |
| **Compras (Purchases)** |                     |                                                   |
| `POST` | `/api/purchases`          | Registra uma nova compra para o usuário logado.   |
| `GET`  | `/api/purchases`          | Retorna todas as compras do usuário logado.     |
| `GET`  | `/api/purchases/{id}`     | Retorna uma compra específica do usuário logado.  |
| `DELETE`| `/api/purchases/{id}`    | Deleta uma compra do usuário logado.              |

## 🛠️ Tecnologias Utilizadas

-   **Framework:** Spring Boot
-   **Linguagem:** Java 21
-   **Segurança:** Spring Security (com Autenticação baseada em Token JWT)
-   **Persistência:** Spring Data JPA (Hibernate)
-   **Banco de Dados:** H2 (desenvolvimento)
-   **Build:** Maven
-   **Testes:** JUnit 5, Mockito, MockMvc, Spring Boot Test

## 🧪 Estratégia de Testes

O projeto adota uma abordagem de testes em múltiplas camadas para garantir a qualidade, segurança e estabilidade do código.

-   **Testes de Unidade:** Focados na camada de serviço (`*ServiceTest.java`). Utilizam **Mockito** para mockar as dependências (como repositórios) e isolar a lógica de negócio, garantindo que as regras sejam aplicadas corretamente.

-   **Testes de API (Integração Web):** Focados na camada de controller (`*ControllerTest.java`). Utilizam `@SpringBootTest` e `MockMvc` para simular requisições HTTP e validar o comportamento dos endpoints de ponta a ponta, incluindo status de resposta, conteúdo JSON e tratamento de erros. A segurança é testada com o auxílio de `@WithMockUser`.

# 📊 API de Votações

API REST para **gerenciar pautas** e **registrar votos** de forma simples, validada e com respostas padronizadas.

---

## ⚙ Tecnologias e Ferramentas

- **Java 21** – Versão utilizada para o desenvolvimento da aplicação.

- **Spring Boot 3.5.6** – Framework principal para construir a API REST.

- **Maven** – Ferramenta de Build e gerenciamento de dependências.

- **Banco de dados H2** – Usado localmente para testes e salvo dentro da pasta `data` para persistência

- **Swagger / OpenAPI** – Documentação interativa para testar endpoints de forma simples.

---

## APLANIArquitetura da Solução

### 1️⃣ Estrutura de Endpoints e Domínio
   - Todos os endpoints de pautas e votações utilizam `tituloPauta` como identificador para facilitar testes e uso via Swagger.
   - O uso de `tituloPauta` torna a API legível e amigável, sem que o usuário precise conhecer *IDs* internos do banco.
   - Em um cenário real, o `id` da pauta poderia ser usado internamente, mantendo a mesma experiência para o cliente.

### 2️⃣ Testes Unitários e Integração
   - Foram criados testes unitários com **JUnit 5** e **Mockito**, cobrindo todos os cenários de criação de pautas, abertura de sessão, votação e cálculo de resultados.
   - Para testes de integração, usamos **MockMvc**, permitindo simular requisições HTTP completas sem levantar o servidor real.
     - **Teste de votos massivos:**
   ```java
IntStream.range(0, TOTAL_VOTOS).forEach(i -> {
String cpf = String.format("%011d", i);
VotoRequestDTO request = new VotoRequestDTO(cpf, "SIM");
    mockMvc.perform(post("/api/v1/pautas/" + pauta.getTituloPauta() + "/votos")
        .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
        });
   ``` 
   - Executa centenas ou milhares de requisições em sequência.
   - Valida o comportamento da API em cenários de alta carga.
   - Serve como teste funcional e como benchmark de performance.

### 3️⃣ Integração com Sistemas Externos (Tarefa Bônus 1)
 - Criado um *Client/Facade Fake* que simula a validação de CPF:
   - Retorna aleatoriamente `"ABLE_TO_VOTE"` ou `"UNABLE_TO_VOTE"`.


### 4️⃣ Performance e Escalabilidade (Tarefa Bônus 2)

- Testes de votos massivos simulam centenas ou milhares de requisições.
- O uso de **H2** salvo na pasta local e **MockMvc** permite medir tempos de resposta e identificar gargalos rapidamente via terminal.
- O design segue boas práticas do Spring, garantindo transações consistentes e baixo overhead em cenários de grande volume de votos.

### 5️⃣ Versionamento (Tarefa Bônus 3)

- Todos os endpoints são versionados via URL: `/api/v1/...`
- Essa estratégia permite evoluir a API sem quebrar clientes existentes.
- Futuras versões poderiam adicionar novos recursos ou alterar respostas, mantendo compatibilidade com clientes antigos.

### 6️⃣ Outras Ferramentas e Boas Práticas

- **Logs:** `SLF4J` + `LoggerFactory` para rastreamento de ações e auditoria.
- **Documentação no Código:** `@JavaDoc` nas classes de serviço e `@Swagger` nos controllers.
- **Validações:** `@Valid` e DTOs para entrada e saída de dados, garantindo integridade antes de chegar à camada de serviço.
- **Tratamento de exceções:** `GlobalExceptionHandler` categoriza os possíveis erros.
- **Banco de dados H2:** configurado em `application.properties` para `mvn spring-boot:run` e em `application-test.properties` para `mvn test`.
- **Inicialização de Interface Gráfica:** o arquivo `LauncherUI` controla se abre a página do Swagger e do H2; quando `mvn test` não abre interface, mas `mvn spring-boot:run` abre no navegador padrão.

---

## 🌐 Base URL

`/api/v1/pautas`

---

## 🛠️ Endpoints

---

### 1️⃣ Criar Pauta
- **POST** `/api/v1/pautas`
- **Descrição:** Cria uma nova pauta de votação.

---

## **Request Body**
| Campo  | Tipo   | Obrigatório | Descrição                                                           |
|--------|--------|-------------|---------------------------------------------------------------------|
| tituloPauta | String | Sim         | Devemos distribuir sacolinhas no Pet Place??                                      |

## **Exemplo Request**
```json
{
  "tituloPauta": "Devemos distribuir sacolinhas no Pet Place?"
}
```
---

## **Códigos de Retorno**

| Código | Descrição                           | Exemplo de Retorno                                            |
| ------ | ----------------------------------- |---------------------------------------------------------------|
| 201    | Pauta criada com sucesso            | `{ "tituloPauta": "Devemos distribuir sacolinhas no Pet Place?" }` |
| 400    | O título é obrigatório              | `{ "tituloPauta": "O título da pauta é obrigatório" }`             |
| 409    | Já existe uma pauta com esse título | `{ "error": "Já existe uma pauta com esse título" }`          |

---

### 2️⃣ Abrir Sessão
- **POST** `/api/v1/pautas/{tituloPauta}/sessoes`
- **Descrição:** Abre uma sessão de votação em uma pauta

---

## **Parâmetros**

| Nome     | Local | Tipo   | Obrigatório | Descrição                        |
| -------- | ----- | ------ | ----------- | -------------------------------- |
| `tituloPauta` | Path  | String | Sim         | Título da pauta a ser consultada |


## **Request Body**
| Campo          | Tipo    | Obrigatório | Descrição                                                                           |
| -------------- | ------- | ----------- |-------------------------------------------------------------------------------------|
| duracaoMinutos | Integer | Não         | Duração da sessão em minutos (padrão: 1, qualquer valor < 1 será substituído por 1) |

---

## **Exemplo Request**
```json
{
  "duracaoMinutos": 1
}
```

---

## Códigos de Retorno

| Código | Descrição                 | Exemplo de Retorno                                                                                                        |
| ------ | ------------------------- |---------------------------------------------------------------------------------------------------------------------------|
| 200    | Sessão aberta com sucesso | `{ "mensagem": "Sessão de votação aberta por 5 minuto(s) para a pauta: 'Devemos' distribuir sacolinhas no Pet Place?'" }` |
| 404    | Pauta não encontrada      | `{ "error:" "Pauta  não encontrada" }`                                                                                    |

---

### 3️⃣ Registrar Voto
- **POST** `/api/v1/pautas/{tituloPauta}/votos`
- **Descrição:** Registra um voto em uma pauta.

---

## **Parâmetros**

| Nome          | Local | Tipo   | Obrigatório | Descrição                        |
| ------------- | ----- | ------ | ----------- | -------------------------------- |
| `tituloPauta` | Path  | String | Sim         | Título da pauta a ser consultada |

## **Request Body**
| Campo   | Tipo   | Obrigatório | Descrição                       |
|---------| ------ | ----------- |---------------------------------|
| cpf     | String | Sim         | CPF do associado (11 dígitos)   |
| escolha | String | Sim         | Valor do voto: "SIM" ou "NAO"   |

---

## **Exemplo Request 1**
```json
{
  "cpf": "12345678900",
  "escolha": "SIM"
}
```

## **Exemplo Request 2**
```json
{
  "cpf": "12345678999",
  "escolha": "NAO"
}
```

---

## **Códigos de Retorno**
| Código | Descrição                        | Exemplo de Retorno                                                                                                                     |
| ------ |----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| 201    | Voto registrado com sucesso      | `{ "mensagem": "Voto 'SIM' registrado com sucesso para pauta 'Devemos distribuir sacolinhas no Pet Place?' no CPF de '12345678900'" }` |
| 400    | CPF inválido                     | `{ "cpf:" "O CPF deve conter exatamente 11 números" }`                                                                                 |
| 400    | Sessão de votação não foi aberta | `{ "error:" "Sessão de votação não foi aberta" }`                                                                                      |
| 400    | Sessão de votação fechada        | `{ "error:" "Sessão de votação fechada" }`                                                                                             |
| 401    | Associado não autorizado a votar | `{ "error:" "Associado não autorizado a votar" }`                                                                                      |
| 404    | Pauta não encontrada             | `{ "error:" "Pauta  não encontrada" }`                                                                                                 |
| 409    | Associado já votou               | `{ "error:" "Associado já votou" }`                                                                                                    |

---

### 4️⃣ Obter Resultado da Pauta
- **GET** `/api/v1/pautas/{tituloPauta}/resultados`
- **Descrição:** Obtém o resultado da votação de uma pauta.

---

## **Parâmetros**

| Nome          | Local | Tipo   | Obrigatório | Descrição                        |
| ------------- | ----- | ------ | ----------- | -------------------------------- |
| `tituloPauta` | Path  | String | Sim         | Título da pauta a ser consultada |

---

## **Códigos de Retorno**
| Código | Descrição                       | Exemplo de Retorno (JSON)                                                                                                         |
| ------ | ------------------------------- |-----------------------------------------------------------------------------------------------------------------------------------|
| 200    | Resultado retornado com sucesso | `{ "tituloPauta": "Devemos distribuir sacolinhas no Pet Place?", "resultado": { "sim": 36, "nao": 64, "status": "REPROVADA" } }`       |
| 200    | Pauta sem nenhum voto           | `{ "tituloPauta": "Seguranca deve monitorar as areas comuns por 24hrs?", "resultado": { "sim": 0, "nao": 0, "status": "SEM_VOTOS" } }` |
| 404    | Pauta não encontrada            | `{ "error:" "Pauta  não encontrada" }`                                                                                            |

---

## Validações Gerais das 4 Rotas

- CPF deve conter exatamente 11 dígitos.
- Escolha do voto deve ser "SIM" ou "NAO".
- Sessão deve estar aberta para aceitar votos.
- Associado deve ser habilitado para votar ("status": "ABLE_TO_VOTE").
- Associado não pode votar mais de uma vez na mesma pauta.

---

## Observações

- Todos os endpoints de POST usam JSON como formato de request e response.
- Todas as rotas estão sob o path `/api/v1/pauta`.
- Status HTTP corretos:
    - `200 OK` — sucesso
    - `201 Created` - criado (Pauta criada ou Voto registrado)
    - `400 Bad Request` — request inválido (ex.: JSON malformado, validação falhou)
    - `404 Not Found` — Pauta não encontrada
    - `409 Conflict` — conflito de negócio (ex.: associado já votou)

---

## Banco de Dados
- Os dados de pautas e votos são persistidos em banco de dados local em arquivo, garantindo que informações não sejam perdidas entre reinicializações do servidor.
- Recomenda-se backup periódico caso a API seja usada em produção.
- Para ambientes de teste, os dados podem ser resetados ou populados automaticamente.
- O formato do banco é transparente para a API; os endpoints continuam funcionando via JSON sem precisar acessar diretamente os arquivos.

## 📂 Exemplos de Banco de Dados

### Tabela: Pauta
| ID | ABERTURA            | DURACAO_MINUTOS | FECHAMENTO          | TITULO_PAUTA                                               |
| -- |---------------------|-----------------|---------------------| ---------------------------------------------------------- |
| 1  | 15/10/2025 21:41:31 | 10              | 15/10/2025 21:51:31 | Devemos distribuir sacolinhas no Pet Place?                |
| 2  | 15/10/2025 21:41:58 | 40              | 15/10/2025 22:21:58 | Entregadores podem entrar dentro do condomínio?            |
| 3  | null                | null            | null                | Segurança deve monitorar as áreas comuns por 24hrs?        |
| 4  | null                | null            | null                | O horário permitido na piscina deve ser reduzido para 21h? |

### Tabela: Voto

| ID | CPF_ID      | ESCOLHA | PAUTA_ID |
|----|-------------|---------|----------|
| 1  | 12345678900 | SIM     | 1        |
| 2  | 12345678901 | SIM     | 1        |
| 3  | 12345678902 | NAO     | 1        |
| 4  | 12345678903 | NAO     | 1        |
| 5  | 12345678923 | SIM     | 1        |

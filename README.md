# 📊 API de Votações

API REST para **gerenciar pautas** e **registrar votos** de forma simples, validada e com respostas padronizadas.

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
| titulo | String | Sim         | Devemos distribuir sacolinhas?                                      |

## **Exemplo Request**
```json
{
  "titulo": "Devemos distribuir sacolinhas no Pet Place?"
}
```
---

## **Códigos de Retorno**

| Código | Descrição                           | Exemplo de Retorno                                   |
| ------ | ----------------------------------- |------------------------------------------------------|
| 201    | Pauta criada com sucesso            | `{ "titulo": "Devemos distribuir sacolinhas?" }`     |
| 400    | O título é obrigatório              | `{ "titulo": "O título da pauta é obrigatório" }`    |
| 409    | Já existe uma pauta com esse título | `{ "error": "Já existe uma pauta com esse título" }` |

---

### 2️⃣ Abrir Sessão
- **POST** `/api/v1/pautas/{titulo}/sessoes`
- **Descrição:** Abre uma sessão de votação em uma pauta

---

## **Parâmetros**

| Nome     | Local | Tipo   | Obrigatório | Descrição                        |
| -------- | ----- | ------ | ----------- | -------------------------------- |
| `titulo` | Path  | String | Sim         | Título da pauta a ser consultada |


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

| Código | Descrição                 | Exemplo de Retorno                                                                        |
| ------ | ------------------------- | ----------------------------------------------------------------------------------------- |
| 200    | Sessão aberta com sucesso | `"Sessão de votação aberta por 5 minuto(s) para a pauta: Devemos distribuir sacolinhas?"` |
| 404    | Pauta não encontrada      | `"Pauta não encontrada"`                                                                  |

---

### 3️⃣ Registrar Voto
- **POST** `/api/v1/pautas/{titulo}/votos`
- **Descrição:** Registra um voto em uma pauta.

---

## **Parâmetros**

| Nome     | Local | Tipo   | Obrigatório | Descrição                        |
| -------- | ----- | ------ | ----------- | -------------------------------- |
| `titulo` | Path  | String | Sim         | Título da pauta a ser consultada |

## **Request Body**
| Campo       | Tipo   | Obrigatório | Descrição                     |
| ----------- | ------ | ----------- | ----------------------------- |
| associadoId | String | Sim         | CPF do associado (11 dígitos) |
| escolha     | String | Sim         | Valor do voto: "SIM" ou "NAO" |

---

## **Exemplo Request 1**
```json
{
  "associadoId": "12345678900",
  "escolha": "SIM"
}
```

## **Exemplo Request 2**
```json
{
  "associadoId": "12345678999",
  "escolha": "NAO"
}
```

---

## **Códigos de Retorno**
| Código | Descrição                        | Exemplo de Retorno                                   |
| ------ | -------------------------------- | ---------------------------------------------------- |
| 201    | Voto registrado com sucesso      | `{ "associadoId": "12345678900", "escolha": "SIM" }` |
| 400    | CPF inválido                     | `"O CPF deve conter exatamente 11 números"`          |
| 401    | Associado não autorizado a votar | `"Associado não autorizado a votar"`                 |
| 404    | Pauta não encontrada             | `"Pauta não encontrada"`                             |
| 409    | Associado já votou               | `"Associado já votou"`                               |

---

### 4️⃣ Obter Resultado da Pauta
- **GET** `/api/v1/pautas/{titulo}/resultados`
- **Descrição:** Obtém o resultado da votação de uma pauta.

---

## **Parâmetros**

| Nome     | Local | Tipo   | Obrigatório | Descrição                        |
| -------- | ----- | ------ | ----------- | -------------------------------- |
| `titulo` | Path  | String | Sim         | Título da pauta a ser consultada |

---

## **Códigos de Retorno**
| Código | Descrição                       | Exemplo de Retorno                                                                            |
| ------ |---------------------------------|-----------------------------------------------------------------------------------------------|
| 200    | Resultado retornado com sucesso | `"36% das pessoas votaram SIM, 64% das pessoas votaram NAO, portanto a pauta está REPROVADA."` |
| 200    | Pauta sem nenhum voto           | `"Nenhum voto registrado para a pauta 'Devemos distribuir sacolinhas?'"`                                                  |
| 404    | Pauta não encontrada            | `"Pauta não encontrada"`                                                                      |

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
    - `201 Created` - criado (Pauta criada ou Voto computado)
    - `400 Bad Request` — request inválido (ex.: JSON malformado, validação falhou)
    - `404 Not Found` — recurso não encontrado
    - `409 Conflict` — conflito de negócio (ex.: associado já votou)
- Os dados de pautas e votos são persistidos em banco de dados local em arquivo, garantindo que informações não sejam perdidas entre reinicializações do servidor.
- Recomenda-se backup periódico caso a API seja usada em produção.
- Para ambientes de teste, os dados podem ser resetados ou populados automaticamente.
- O formato do banco é transparente para a API; os endpoints continuam funcionando via JSON sem precisar acessar diretamente os arquivos.
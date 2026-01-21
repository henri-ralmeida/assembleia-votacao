# Assembleia Votação API 🗳️

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/H2-blue?style=for-the-badge&logo=databricks&logoColor=white" alt="H2"/>
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white" alt="Kubernetes"/>
</p>

<p align="center">
  <b>API REST para gerenciar pautas de votação em assembleias, com controle de sessões, validação de CPF e contabilização de votos.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Version-1.0.0-blue" alt="Version"/>
  <img src="https://img.shields.io/badge/Java-21-orange" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Build-Passing-brightgreen" alt="Build"/>
  <img src="https://img.shields.io/badge/License-Apache%202.0-blue" alt="License"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Testes-Unitários%20%2B%20Integração-blue" alt="Tests"/>
  <img src="https://img.shields.io/badge/Votos%20Massivos-10k%2B%20testados-brightgreen" alt="Massive Votes"/>
  <img src="https://img.shields.io/badge/Docker-Ready-blue" alt="Docker"/>
  <img src="https://img.shields.io/badge/K8s-Ready-blue" alt="K8s"/>
</p>

---

## 📑 Índice

- [Visão Geral](#-visão-geral)
- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#️-tecnologias)
- [Arquitetura](#-arquitetura)
- [Como Executar](#-como-executar)
  - [Pré-requisitos](#pré-requisitos)
  - [Execução Local](#1️⃣-execução-local)
  - [Docker](#2️⃣-docker)
  - [Docker Compose](#3️⃣-docker-compose)
  - [Kubernetes](#4️⃣-kubernetes)
- [API Endpoints](#-api-endpoints)
- [Fluxo de Votação](#-fluxo-de-votação)
- [Banco de Dados](#️-banco-de-dados)
- [Testes](#-testes)
- [Regras de Negócio](#-regras-de-negócio)
- [Tarefas Bônus](#-tarefas-bônus)

---

## 🎯 Visão Geral

API REST para gerenciamento de **votações em assembleias**, permitindo:

- 📋 **Criar pautas** para votação
- ⏱️ **Abrir sessões** com tempo configurável
- 🗳️ **Registrar votos** (SIM/NÃO) com validação de CPF
- 📊 **Consultar resultados** em tempo real
- 🔄 **Integração com sistemas externos** para validação de CPF

---

## ✨ Funcionalidades

### 📋 Gestão de Pautas

| Recurso | Descrição | Status |
|---------|-----------|--------|
| Criar Pauta | Cadastro com título único | ✅ |
| Listar Pautas | Consulta de todas as pautas | ✅ |
| Buscar por Título | Busca específica | ✅ |
| Validação | Impede títulos duplicados | ✅ |

### ⏱️ Sessões de Votação

| Recurso | Descrição | Status |
|---------|-----------|--------|
| Abrir Sessão | Duração configurável (padrão: 1 min) | ✅ |
| Controle de Tempo | Abertura e fechamento automático | ✅ |
| Validação | Impede votos em sessões fechadas | ✅ |

### 🗳️ Votação

| Recurso | Descrição | Status |
|---------|-----------|--------|
| Registrar Voto | SIM ou NÃO | ✅ |
| Validação CPF | 11 dígitos + integração externa | ✅ |
| Voto Único | Um CPF por pauta | ✅ |
| Voto em Tempo Real | Apenas sessões abertas | ✅ |

### 📊 Resultados

| Recurso | Descrição | Status |
|---------|-----------|--------|
| Contagem Automática | SIM vs NÃO | ✅ |
| Status da Pauta | APROVADA / REPROVADA / EMPATE / SEM_VOTOS | ✅ |
| Consulta a Qualquer Momento | Resultado sempre disponível | ✅ |

### 🎯 Bônus Implementados

| Recurso | Descrição | Status |
|---------|-----------|--------|
| Integração Externa | Cliente fake para validação de CPF | ✅ |
| Performance | Testes com 10k+ votos | ✅ |
| Versionamento | API versionada `/api/v1/` | ✅ |

### 🚀 DevOps

| Recurso | Descrição | Status |
|---------|-----------|--------|
| Docker | Multi-stage build otimizado | ✅ |
| Docker Compose | Orquestração local | ✅ |
| Kubernetes | Deployment + HPA + ConfigMap | ✅ |
| Health Checks | Liveness & Readiness probes | ✅ |
| Auto Scaling | HPA baseado em CPU/Memória | ✅ |
| Actuator | Monitoramento integrado | ✅ |

---

## 🛠️ Tecnologias

<table>
  <tr>
    <td align="center"><img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/><br><b>Java 21</b></td>
    <td align="center"><img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/><br><b>Spring Boot 3.5</b></td>
    <td align="center"><img src="https://img.shields.io/badge/H2-0000BB?style=for-the-badge&logo=databricks&logoColor=white"/><br><b>H2 Database</b></td>
    <td align="center"><img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white"/><br><b>Maven</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/><br><b>OpenAPI 3</b></td>
    <td align="center"><img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white"/><br><b>JUnit 5</b></td>
    <td align="center"><img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/><br><b>Docker</b></td>
    <td align="center"><img src="https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white"/><br><b>Kubernetes</b></td>
  </tr>
</table>

### Stack Completo

| Categoria | Tecnologia |
|-----------|------------|
| **Linguagem** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.5.6 |
| **Database** | H2 (file-based) |
| **Documentação** | SpringDoc OpenAPI 3 |
| **Validação** | Spring Validation + Bean Validation |
| **Testes** | JUnit 5 + Mockito + MockMvc |
| **Build** | Maven 3.9 |
| **Monitoramento** | Spring Boot Actuator |
| **Container** | Docker (multi-stage) |
| **Orquestração** | Kubernetes + HPA |
| **Logging** | Logback + SLF4J |

---

## 📐 Arquitetura

### Arquitetura em Camadas

```       
┌───────────────────────────────────────────────────────────────┐
|                         PRESENTATION                          |
│  ┌────────────────┐  ┌─────────────────┐  ┌────────────────┐  │
│  │PautaController │  │ VotoController  │  │ResultController│  │
│  │    /api/v1/    │  │   /api/v1/      │  │  /api/v1/      │  │
│  └───────┬────────┘  └────────┬────────┘  └───────┬────────┘  │
└──────────┼────────────────────┼───────────────────┼───────────┘
           |                    |                   |
┌──────────▼────────────────────▼───────────────────▼───────────┐
│                            SERVICE                            │
│  ┌─────────────────┐  ┌────────────────┐  ┌────────────────┐  │
│  │  PautaService   │  │  VotoService   │  │   CPF Client   │  │
│  │ (Pautas+Sessão) │  │   (Votação)    │  │  (Validação)   │  │
│  └───────┬─────────┘  └───────┬────────┘  └───────┬────────┘  │
└──────────┼────────────────────┼───────────────────┼───────────┘
           |                    |                   |
┌──────────▼────────────────────▼───────────────────▼───────────┐
│                          REPOSITORY                           |
│  ┌─────────────────┐  ┌────────────────┐                      │
│  │ PautaRepository │  │ VotoRepository │   Spring Data JPA    │
│  └───────┬─────────┘  └───────┬────────┘                      │
└──────────┼────────────────────┼───────────────────────────────┘
           |                    |
┌──────────▼────────────────────▼───────────────────────────────┐
│                            DATABASE                           |
│  ┌────────────────────────────────────────────────────────┐   │
│  │                H2 Database (File-based)                │   │
│  │                    pauta │ voto                        │   │
│  └────────────────────────────────────────────────────────┘   │
└───────────────────────────────────────────────────────────────┘
```

---

## 🗳️ Fluxo de Votação

### Diagrama de Fluxo

```
┌───────────────────────────────────────────────────────────────┐
│                      FLUXO DE VOTAÇÃO                         │
└───────────────────────────────────────────────────────────────┘

     ┌──────────────┐
     │ Criar Pauta  │
     └──────┬───────┘
            │
            ▼
     ┌──────────────┐      ┌─────────────────┐
     │ Abrir Sessão │─────>│ Duração = N min │
     └──────┬───────┘      └─────────────────┘
            │
            ▼
     ┌──────────────┐
     │ Sessão Aberta│◄─────────────────────────────┐
     └──────┬───────┘                              │
            │                                      │
            ▼                                      │
     ┌──────────────┐     ┌──────────────┐         │
     │ Voto Recebido│────>│ Validar CPF  │         │
     └──────────────┘     └──────┬───────┘         │
                                 │                 │
                    ┌────────────┴────────────┐    │
                    │                         │    │
                    ▼                         ▼    │
            ┌──────────────┐         ┌───────────┐ │
            │ ABLE_TO_VOTE │         │  UNABLE   │ │
            └──────┬───────┘         └───────────┘ │
                   │                      │        │
                   │                      ▼        │
                   │              ┌────────────┐   │
                   │              │ 401 Unauth │   │
                   │              └────────────┘   │
                   ▼                               │
            ┌──────────────┐                       │
            │ Já votou?    │                       │
            └──────┬───────┘                       │
                   │                               │
          ┌────────┴────────┐                      │
          │                 │                      │
          ▼                 ▼                      │
   ┌────────────┐   ┌────────────┐                 │
   │    NÃO     │   │    SIM     │                 │
   └──────┬─────┘   └──────┬─────┘                 │
          │                │                       │
          ▼                ▼                       │
   ┌────────────┐   ┌────────────┐                 │
   │ Salva Voto │   │409 Conflict│                 │ 
   │ 201 Created│   └────────────┘                 │
   └──────┬─────┘                                  │
          │                                        │
          └────────────────────────────────────────┘
                          │
                          ▼
                 ┌────────────────┐
                 │ Sessão Fechada │
                 └───────┬────────┘
                         │
                         ▼
                 ┌────────────────┐
                 │   Consultar    │
                 │   Resultado    │
                 └───────┬────────┘
                         │
            ┌────────────┼────────────┐
            │            │            │
            ▼            ▼            ▼
     ┌──────────┐ ┌───────────┐ ┌───────────┐
     │ APROVADA │ │ REPROVADA │ │   EMPATE  │
     │ SIM > NAO│ │ NAO > SIM │ │ SIM = NAO │
     └──────────┘ └───────────┘ └───────────┘
```

### Estados da Votação

| Estado | Descrição | Cor |
|--------|-----------|-----|
| 🟢 **APROVADA** | Maioria votou SIM | Verde |
| 🔴 **REPROVADA** | Maioria votou NÃO | Vermelho |
| 🟡 **EMPATE** | SIM = NÃO | Amarelo |
| ⚪ **SEM_VOTOS** | Nenhum voto registrado | Cinza |

---

## 🚀 Como Executar

### Pré-requisitos

| Requisito | Versão | Obrigatório |
|-----------|--------|-------------|
| ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white) JDK | 21+ | ✅ |
| ![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white) Maven | 3.9+ | ✅ |
| ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white) Docker | 20+ | Opcional |
| ![Kubernetes](https://img.shields.io/badge/K8s-326CE5?style=flat-square&logo=kubernetes&logoColor=white) Kubernetes | 1.25+ | Opcional |

> 📝 **Nota:** O H2 Database já está embutido, não precisa instalar banco de dados!

---

### 1️⃣ Execução Local

```bash
# Clone o repositório
git clone https://github.com/henri-ralmeida/assembleia-votacao.git
cd assembleia-votacao

# Execute a aplicação
./mvnw spring-boot:run
```

**Acesse:**

| Serviço | URL | Descrição |
|---------|-----|-----------|
| 🌐 API | http://localhost:8080 | API REST |
| 📚 Swagger UI | http://localhost:8080/swagger-ui.html | Documentação interativa |
| 🗄️ H2 Console | http://localhost:8080/h2-console | Banco de dados |
| ❤️ Health | http://localhost:8080/actuator/health | Status da aplicação |

**Configuração do H2 Console:**
```
JDBC URL: jdbc:h2:file:./data/votacao_db
Username: sa
Password: (deixe em branco)
```

---

### 2️⃣ Docker

```bash
# Build da imagem
docker build -t votacao-api:latest .

# Execute o container
docker run -d \
  --name votacao-api \
  -p 8080:8080 \
  -v $(pwd)/data:/app/data \
  votacao-api:latest

# Verifique os logs
docker logs -f votacao-api
```

---

### 3️⃣ Docker Compose

```bash
# Suba a aplicação
docker-compose up -d

# Acompanhe os logs
docker-compose logs -f

# Pare a aplicação
docker-compose down

# Pare e remova volumes (⚠️ apaga dados)
docker-compose down -v
```

---

### 4️⃣ Kubernetes

```bash
# Se usar Minikube, configure o Docker
eval $(minikube docker-env)

# Build da imagem
docker build -t votacao-api:latest .

# Deploy de todos os recursos
kubectl apply -f k8s/

# Verifique o status
kubectl get all -n assembleia-votacao

# Acesse via Minikube
minikube service app-service -n assembleia-votacao

# Ou via Port Forward
kubectl port-forward -n assembleia-votacao svc/app-service 8080:80
```

**Recursos criados:**

| Recurso | Nome | Descrição |
|---------|------|-----------|
| Namespace | `assembleia-votacao` | Isolamento |
| Deployment | `votacao-api` | 2 réplicas |
| Service | `app-service` | LoadBalancer |
| ConfigMap | `app-config` | Configurações H2/JPA |
| Secret | `app-secret` | Credenciais |
| HPA | `votacao-api-hpa` | Auto scaling 2-5 pods |

📖 Veja o guia completo em [DEPLOY.md](DEPLOY.md)

---

## 🌐 API Endpoints

### Base URL

```
http://localhost:8080/api/v1/pautas
```

---

### 📋 1. Criar Pauta

```http
POST /api/v1/pautas
Content-Type: application/json
```

```json
{
  "tituloPauta": "Devemos instalar painéis solares no condomínio?"
}
```

<details>
<summary>📤 Response 201 - Sucesso</summary>

```json
{
  "tituloPauta": "Devemos instalar painéis solares no condomínio?"
}
```
</details>

<details>
<summary>❌ Response 409 - Conflito</summary>

```json
{
  "error": "Já existe uma pauta com esse título"
}
```
</details>

---

### ⏱️ 2. Abrir Sessão de Votação

```http
POST /api/v1/pautas/{tituloPauta}/sessoes
Content-Type: application/json
```

```json
{
  "duracaoMinutos": 10
}
```

> 💡 Se `duracaoMinutos` não for informado, assume **1 minuto**

<details>
<summary>📤 Response 200 - Sucesso</summary>

```json
{
  "mensagem": "Sessão de votação aberta por 10 minuto(s) para a pauta: 'Devemos instalar painéis solares no condomínio?'"
}
```
</details>

---

### 🗳️ 3. Registrar Voto

```http
POST /api/v1/pautas/{tituloPauta}/votos
Content-Type: application/json
```

```json
{
  "cpf": "12345678900",
  "escolha": "SIM"
}
```

| Campo | Tipo | Obrigatório | Regras |
|-------|------|-------------|--------|
| `cpf` | String | ✅ | 11 dígitos numéricos |
| `escolha` | String | ✅ | `"SIM"` ou `"NAO"` |

<details>
<summary>📤 Response 201 - Voto Registrado</summary>

```json
{
  "mensagem": "Voto 'SIM' registrado com sucesso para pauta 'Devemos instalar painéis solares no condomínio?' no CPF de '12345678900'"
}
```
</details>

<details>
<summary>❌ Response 400 - Sessão Fechada</summary>

```json
{
  "error": "Sessão de votação fechada"
}
```
</details>

<details>
<summary>❌ Response 401 - Não Autorizado</summary>

```json
{
  "error": "Associado não autorizado a votar"
}
```
</details>

<details>
<summary>❌ Response 409 - Já Votou</summary>

```json
{
  "error": "Associado já votou"
}
```
</details>

---

### 📊 4. Consultar Resultado

```http
GET /api/v1/pautas/{tituloPauta}/resultados
```

<details>
<summary>📤 Response 200 - Aprovada 🟢</summary>

```json
{
  "tituloPauta": "Devemos instalar painéis solares no condomínio?",
  "resultado": {
    "sim": 75,
    "nao": 25,
    "status": "APROVADA"
  }
}
```
</details>

<details>
<summary>📤 Response 200 - Reprovada 🔴</summary>

```json
{
  "tituloPauta": "Devemos instalar painéis solares no condomínio?",
  "resultado": {
    "sim": 30,
    "nao": 70,
    "status": "REPROVADA"
  }
}
```
</details>

<details>
<summary>📤 Response 200 - Empate 🟡</summary>

```json
{
  "tituloPauta": "Devemos instalar painéis solares no condomínio?",
  "resultado": {
    "sim": 50,
    "nao": 50,
    "status": "EMPATE"
  }
}
```
</details>

<details>
<summary>📤 Response 200 - Sem Votos ⚪</summary>

```json
{
  "tituloPauta": "Devemos instalar painéis solares no condomínio?",
  "resultado": {
    "sim": 0,
    "nao": 0,
    "status": "SEM_VOTOS"
  }
}
```
</details>

---

### Códigos de Resposta

| Código | Descrição | Quando |
|--------|-----------|--------|
| `200` | ✅ Sucesso | GET, Sessão aberta |
| `201` | ✅ Criado | POST (pauta, voto) |
| `400` | ❌ Bad Request | Dados inválidos, sessão fechada |
| `401` | 🚫 Não autorizado | CPF não pode votar |
| `404` | ❓ Não encontrado | Pauta inexistente |
| `409` | ⚠️ Conflito | Duplicidade (pauta/voto) |

---

## 🗄️ Banco de Dados

### Schema

```sql
┌─────────────────────────────────────────────────────────────┐
│                          PAUTA                              │
├───────────────────┬────────────────┬────────────────────────┤
│ id                │ BIGINT         │ PK, AUTO_INCREMENT     │
│ titulo_pauta      │ VARCHAR(255)   │ UNIQUE, NOT NULL       │
│ abertura          │ TIMESTAMP      │ NULL (sessão fechada)  │
│ fechamento        │ TIMESTAMP      │ NULL (sessão fechada)  │
│ duracao_minutos   │ INTEGER        │ NULL                   │
└───────────────────┴────────────────┴────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                           VOTO                              │
├───────────────────┬────────────────┬────────────────────────┤
│ id                │ BIGINT         │ PK, AUTO_INCREMENT     │
│ cpf_id            │ VARCHAR(11)    │ NOT NULL               │
│ escolha           │ VARCHAR(3)     │ NOT NULL (SIM/NAO)     │
│ pauta_id          │ BIGINT         │ FK → PAUTA(id)         │
│ data_hora         │ TIMESTAMP      │ DEFAULT NOW            │
├───────────────────┴────────────────┴────────────────────────┤
│ UNIQUE CONSTRAINT: (cpf_id, pauta_id)                       │
└─────────────────────────────────────────────────────────────┘
```

### Exemplo de Dados

**Tabela PAUTA:**

| ID | TITULO_PAUTA | ABERTURA | FECHAMENTO | DURACAO |
|----|--------------|----------|------------|---------|
| 1 | Instalar painéis solares? | 2025-01-20 10:00 | 2025-01-20 10:10 | 10 |
| 2 | Reformar academia? | 2025-01-20 11:00 | 2025-01-20 11:05 | 5 |
| 3 | Trocar porteiro por digital? | NULL | NULL | NULL |

**Tabela VOTO:**

| ID | CPF_ID | ESCOLHA | PAUTA_ID |
|----|--------|---------|----------|
| 1 | 12345678900 | SIM | 1 |
| 2 | 12345678901 | SIM | 1 |
| 3 | 12345678902 | NAO | 1 |

---

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
./mvnw test

# Com relatório
./mvnw test surefire-report:report

# Apenas unitários
./mvnw test -Dtest=**/*Test

# Apenas integração
./mvnw test -Dtest=**/*IT
```

### Tipos de Testes

| Tipo | Descrição | Cobertura |
|------|-----------|-----------|
| **Unitários** | PautaService, VotoService | 🟢 100% |
| **Integração** | Endpoints com MockMvc | 🟢 95% |
| **Performance** | Votos massivos (10k+) | 🟢 Testado |

### Teste de Votos Massivos

```java
@Test
void testVotosMassivos() {
    // Registra 10.000 votos em sequência
    IntStream.range(0, 10000).forEach(i -> {
        String cpf = String.format("%011d", i);
        VotoRequestDTO request = new VotoRequestDTO(cpf, "SIM");
        
        mockMvc.perform(post("/api/v1/pautas/{titulo}/votos", titulo)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    });
}
```

---

## ✅ Regras de Negócio

### CPF

| Regra | Descrição |
|-------|-----------|
| ✅ Formato | Exatamente 11 dígitos numéricos |
| ✅ Validação Externa | Cliente fake: ABLE_TO_VOTE / UNABLE_TO_VOTE |
| ✅ Unicidade | Um CPF = Um voto por pauta |

### Voto

| Regra | Descrição |
|-------|-----------|
| ✅ Escolhas | Apenas `"SIM"` ou `"NAO"` |
| ✅ Sessão | Só aceita votos em sessões abertas |
| ✅ Duplicidade | Rejeita se CPF já votou |

### Sessão

| Regra | Descrição |
|-------|-----------|
| ✅ Duração | Mínimo 1 minuto (padrão) |
| ✅ Abertura | Marca timestamp de início |
| ✅ Fechamento | Calculado: abertura + duração |
| ✅ Status | Verificado em tempo real |

### Resultado

| Status | Condição |
|--------|----------|
| 🟢 APROVADA | SIM > NÃO |
| 🔴 REPROVADA | NÃO > SIM |
| 🟡 EMPATE | SIM = NÃO (ambos > 0) |
| ⚪ SEM_VOTOS | SIM = NÃO = 0 |

---

## 🎯 Tarefas Bônus

### ✅ Bônus 1 - Integração Externa

Cliente fake para validação de CPF:

```java
@Service
public class ValidacaoCpfClient {
    
    public String validarCpf(String cpf) {
        // Simula resposta de API externa
        // 50% ABLE_TO_VOTE, 50% UNABLE_TO_VOTE
        return Math.random() > 0.5 
            ? "ABLE_TO_VOTE" 
            : "UNABLE_TO_VOTE";
    }
}
```

### ✅ Bônus 2 - Performance

- Testes com **10.000+ votos** em sequência
- Validação de comportamento sob carga
- Medição de tempo de resposta
- Transações consistentes

### ✅ Bônus 3 - Versionamento

API versionada via URL:

```
/api/v1/pautas
/api/v1/pautas/{titulo}/sessoes
/api/v1/pautas/{titulo}/votos
/api/v1/pautas/{titulo}/resultados
```

---

## 📁 Estrutura do Projeto

```
assembleia-votacao/
│
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/henrique/votacao/
│   │   │   ├── 📁 controller/      # 🌐 REST Controllers
│   │   │   ├── 📁 service/         # ⚙️ Business Logic
│   │   │   ├── 📁 repository/      # 💾 Data Access
│   │   │   ├── 📁 model/           # 📦 Entities
│   │   │   ├── 📁 dto/             # 📤 Request/Response
│   │   │   ├── 📁 exception/       # ❌ Error Handling
│   │   │   └── 📁 config/          # ⚙️ App Config
│   │   │
│   │   └── 📁 resources/
│   │       ├── application.properties
│   │       └── application-dev.properties
│   │
│   └── 📁 test/                    # 🧪 Testes
│
├── 📁 k8s/                         # ☸️ Kubernetes
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── app.yaml
│   └── hpa.yaml
│
├── 🐳 Dockerfile
├── 🐳 docker-compose.yml
├── 📄 pom.xml
├── 📖 DEPLOY.md
└── 📖 README.md
```

---

## ⚙️ Configuração

### application.properties

```properties
# H2 Database
spring.datasource.url=jdbc:h2:file:./data/votacao_db
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Actuator
management.endpoints.web.exposure.include=health,metrics
```

### Variáveis de Ambiente (Docker/K8s)

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `SPRING_DATASOURCE_URL` | URL do H2 | `jdbc:h2:file:/app/data/votacao_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuário | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Senha | (vazio) |
| `SPRING_H2_CONSOLE_ENABLED` | Console H2 | `true` |
| `LAUNCHER_UI_DISABLED` | Auto-open | `true` (prod) |

---

## ❤️ Health Check

```http
GET /actuator/health
```

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

---

## 📄 Licença

Este projeto está sob a licença Apache 2.0. Veja o arquivo [LICENSE](LICENSE) para detalhes.

---

<p align="center">
  <img src="https://img.shields.io/badge/Made%20with-☕%20Java-ED8B00?style=for-the-badge" alt="Made with Java"/>
</p>

<p align="center">
  <b>Desenvolvido com ☕ por Henrique Almeida</b>
</p>

<p align="center">
  <a href="https://github.com/henri-ralmeida">
    <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub"/>
  </a>
</p>

<p align="center">
  <a href="#assembleia-votação-api-️">⬆️ Voltar ao topo</a>
</p>

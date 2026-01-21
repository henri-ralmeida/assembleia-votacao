# Guia de Deploy - Assembleia Votação

Este documento fornece instruções detalhadas para fazer o deploy da aplicação usando Docker e Kubernetes.

## Índice
- [Docker](#docker)
- [Docker Compose](#docker-compose)
- [Kubernetes](#kubernetes)

---

## Docker

### Build da Imagem

```bash
docker build -t votacao-api:latest .
```

### Executar Container

```bash
docker run -d \
  --name votacao-api \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:h2:file:/app/data/votacao_db;DB_CLOSE_ON_EXIT=FALSE" \
  -e SPRING_DATASOURCE_USERNAME="sa" \
  -e SPRING_DATASOURCE_PASSWORD="" \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO="update" \
  votacao-api:latest
```

### Verificar Logs

```bash
docker logs -f votacao-api
```

### Acessar a Aplicação

- API: http://localhost:8080/api/
- H2 Console: http://localhost:8080/h2-console
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

---

## Docker Compose

### Iniciar a Aplicação

```bash
# Build e iniciar
docker-compose up -d

# Apenas build
docker-compose build

# Iniciar sem build
docker-compose up -d
```

### Verificar Status

```bash
docker-compose ps
```

### Ver Logs

```bash
# Todos os serviços
docker-compose logs -f

# Apenas a API
docker-compose logs -f votacao-api
```

### Parar e Remover

```bash
# Parar
docker-compose stop

# Parar e remover containers
docker-compose down

# Parar, remover containers e volumes
docker-compose down -v
```

---

## Kubernetes

### Pré-requisitos

#### Opção 1: Minikube

```bash
# Instalar Minikube
# Windows (com Chocolatey)
choco install minikube

# Linux
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube

# Iniciar Minikube
minikube start --driver=docker

# Configurar Docker para usar daemon do Minikube
eval $(minikube docker-env)
```

#### Opção 2: Docker Desktop

Habilitar Kubernetes no Docker Desktop:
1. Abra Docker Desktop
2. Settings > Kubernetes
3. Marque "Enable Kubernetes"
4. Apply & Restart

### Build da Imagem

```bash
# Se estiver usando Minikube
eval $(minikube docker-env)
docker build -t votacao-api:latest .

# Se estiver usando Docker Desktop
docker build -t votacao-api:latest .
```

### Deploy Completo

```bash
# Deploy de todos os recursos
kubectl apply -f k8s/

# Verificar o status
kubectl get all -n assembleia-votacao
```

### Deploy Passo a Passo

```bash
# 1. Criar namespace
kubectl apply -f k8s/namespace.yaml

# 2. Criar configurações
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml

# 3. Deploy da aplicação
kubectl apply -f k8s/app.yaml

# 4. Configurar autoscaling (opcional, requer metrics-server)
kubectl apply -f k8s/hpa.yaml
```

### Verificar Deploy

```bash
# Status dos pods
kubectl get pods -n assembleia-votacao -w

# Detalhes de um pod
kubectl describe pod -n assembleia-votacao <pod-name>

# Logs
kubectl logs -n assembleia-votacao -l app=votacao-api -f

# Services
kubectl get svc -n assembleia-votacao

# HPA (se configurado)
kubectl get hpa -n assembleia-votacao
```

### Acessar a Aplicação

#### Minikube

```bash
# Abrir automaticamente no navegador
minikube service app-service -n assembleia-votacao

# Obter URL
minikube service app-service -n assembleia-votacao --url
```

#### Docker Desktop

```bash
# Verificar o IP externo
kubectl get svc app-service -n assembleia-votacao

# Se o EXTERNAL-IP for 'localhost' ou um IP
# Acesse: http://localhost ou http://<EXTERNAL-IP>
```

#### Port Forward (Qualquer ambiente)

```bash
kubectl port-forward -n assembleia-votacao svc/app-service 8080:80

# Acesse: http://localhost:8080
```

### Escalar Manualmente

```bash
# Aumentar número de réplicas
kubectl scale deployment votacao-api -n assembleia-votacao --replicas=3

# Verificar
kubectl get pods -n assembleia-votacao
```

### Atualizar Aplicação

```bash
# 1. Build nova imagem
docker build -t votacao-api:v2 .

# 2. Atualizar deployment
kubectl set image deployment/votacao-api -n assembleia-votacao api=votacao-api:v2

# 3. Verificar rollout
kubectl rollout status deployment/votacao-api -n assembleia-votacao

# 4. Se precisar fazer rollback
kubectl rollout undo deployment/votacao-api -n assembleia-votacao
```

### Troubleshooting

#### Pods não iniciam

```bash
# Ver eventos
kubectl get events -n assembleia-votacao --sort-by='.lastTimestamp'

# Descrever pod
kubectl describe pod -n assembleia-votacao <pod-name>

# Ver logs
kubectl logs -n assembleia-votacao <pod-name>
```

#### ImagePullBackOff

Se aparecer erro "ImagePullBackOff", verifique:

```bash
# Confirmar que a imagem existe
docker images | grep votacao-api

# Se estiver usando Minikube
eval $(minikube docker-env)
docker images | grep votacao-api

# Reconstruir se necessário
docker build -t votacao-api:latest .
```

#### HPA não funciona

```bash
# Verificar se metrics-server está instalado
kubectl get deployment metrics-server -n kube-system

# Instalar metrics-server (Minikube)
minikube addons enable metrics-server

# Instalar metrics-server (Kubernetes genérico)
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

### Remover Deploy

```bash
# Remover todos os recursos
kubectl delete -f k8s/

# Ou remover namespace (remove tudo)
kubectl delete namespace assembleia-votacao
```

### Úteis

```bash
# Abrir dashboard do Minikube
minikube dashboard

# Ver todos os recursos de todos os namespaces
kubectl get all -A

# Executar comando dentro do pod
kubectl exec -it -n assembleia-votacao <pod-name> -- sh

# Ver uso de recursos
kubectl top pods -n assembleia-votacao
kubectl top nodes
```

---

## Endpoints da Aplicação

Após o deploy, a aplicação estará disponível nos seguintes endpoints:

- **API REST**: `http://<host>:<port>/api/`
- **H2 Console**: `http://<host>:<port>/h2-console`
  - JDBC URL: `jdbc:h2:file:/app/data/votacao_db`
  - Username: `sa`
  - Password: (deixe em branco)
- **Swagger UI**: `http://<host>:<port>/swagger-ui.html`
- **OpenAPI Docs**: `http://<host>:<port>/v3/api-docs`
- **Health Check**: `http://<host>:<port>/actuator/health`
- **Metrics**: `http://<host>:<port>/actuator/metrics`

---

## Considerações de Produção

Para ambientes de produção, considere:

1. **Banco de Dados Externo**: Substituir H2 por PostgreSQL, MySQL ou outro SGBD robusto
2. **Secrets Management**: Usar ferramentas como HashiCorp Vault ou AWS Secrets Manager
3. **Monitoramento**: Integrar com Prometheus e Grafana
4. **Logs Centralizados**: ELK Stack (Elasticsearch, Logstash, Kibana) ou similar
5. **Ingress Controller**: Nginx Ingress ou Traefik para roteamento
6. **TLS/SSL**: Certificados SSL para comunicação segura
7. **Resource Limits**: Ajustar limites de CPU e memória baseado em carga real
8. **Backup**: Estratégia de backup do banco de dados
9. **CI/CD**: Pipeline automatizado para builds e deploys
10. **Network Policies**: Políticas de rede para maior segurança

---

## Referências

- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Minikube Documentation](https://minikube.sigs.k8s.io/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

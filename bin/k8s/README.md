# Deploy Kubernetes - Assembleia Votação

Este diretório contém as configurações do Kubernetes para o projeto Assembleia Votação.

## Estrutura

```
k8s/
├── namespace.yaml   # Namespace do projeto
├── configmap.yaml   # Configurações da aplicação
├── secret.yaml      # Credenciais (usuário/senha H2)
├── app.yaml         # Deployment e Service
└── hpa.yaml         # Horizontal Pod Autoscaler
```

## Pré-requisitos

- Docker instalado
- Kubernetes cluster configurado (minikube, k3s, Docker Desktop, etc.)
- kubectl configurado

## Build da Imagem Docker

Antes de fazer o deploy no Kubernetes, você precisa construir a imagem Docker:

```bash
# No diretório raiz do projeto
docker build -t votacao-api:latest .
```

Se estiver usando Minikube, configure o Docker para usar o daemon do Minikube:

```bash
eval $(minikube docker-env)
docker build -t votacao-api:latest .
```

## Deploy no Kubernetes

### 1. Criar o namespace
```bash
kubectl apply -f k8s/namespace.yaml
```

### 2. Criar o ConfigMap e Secret
```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
```

### 3. Deploy da aplicação
```bash
kubectl apply -f k8s/app.yaml
```

### 4. Configurar autoscaling (opcional)
```bash
kubectl apply -f k8s/hpa.yaml
```

### Deploy completo (todos os recursos de uma vez)
```bash
kubectl apply -f k8s/
```

## Verificar o Deploy

```bash
# Verificar o status dos pods
kubectl get pods -n assembleia-votacao

# Verificar os services
kubectl get svc -n assembleia-votacao

# Ver logs da aplicação
kubectl logs -n assembleia-votacao -l app=votacao-api -f

# Verificar o HPA
kubectl get hpa -n assembleia-votacao
```

## Acessar a Aplicação

### Se estiver usando LoadBalancer (Docker Desktop, Cloud)
```bash
kubectl get svc app-service -n assembleia-votacao
# Use o EXTERNAL-IP mostrado
```

### Se estiver usando Minikube
```bash
minikube service app-service -n assembleia-votacao
```

### Port Forward (alternativa)
```bash
kubectl port-forward -n assembleia-votacao svc/app-service 8080:80
# Acesse: http://localhost:8080
```

## Endpoints Disponíveis

- API: `http://<service-ip>/api/...`
- H2 Console: `http://<service-ip>/h2-console`
- Swagger UI: `http://<service-ip>/swagger-ui.html`
- Health Check: `http://<service-ip>/actuator/health`

## Configurações

### Recursos
- **Requests**: 512Mi RAM, 250m CPU
- **Limits**: 1Gi RAM, 500m CPU

### Replicas
- **Mínimo**: 2 réplicas
- **Máximo (HPA)**: 5 réplicas
- **Autoscaling**: CPU > 70% ou Memory > 80%

### Health Checks
- **Liveness**: `/actuator/health` (inicia após 40s)
- **Readiness**: `/actuator/health` (inicia após 20s)

## Persistência

O banco de dados H2 está configurado para persistir em `/app/data` dentro do container. 
Por padrão, usa `emptyDir` (dados são perdidos se o pod for deletado).

### Para adicionar persistência (opcional)

Edite o `app.yaml` e substitua:

```yaml
volumes:
- name: h2-data
  emptyDir: {}
```

Por:

```yaml
volumes:
- name: h2-data
  persistentVolumeClaim:
    claimName: h2-pvc
```

E crie um PersistentVolumeClaim (`pvc.yaml`):

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: h2-pvc
  namespace: assembleia-votacao
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

## Remover o Deploy

```bash
# Remover todos os recursos
kubectl delete -f k8s/

# Ou remover apenas o namespace (remove tudo dentro dele)
kubectl delete namespace assembleia-votacao
```

## Troubleshooting

### Pods não iniciam
```bash
kubectl describe pod -n assembleia-votacao <pod-name>
kubectl logs -n assembleia-votacao <pod-name>
```

### Imagem não encontrada
Certifique-se de que a imagem foi construída corretamente:
```bash
docker images | grep votacao-api
```

Se estiver usando Minikube:
```bash
eval $(minikube docker-env)
docker images | grep votacao-api
```

### Service não acessível
Verifique os endpoints:
```bash
kubectl get endpoints -n assembleia-votacao
```

## Notas

- O projeto usa banco de dados H2 em arquivo, ideal para desenvolvimento
- Para produção, considere usar um banco de dados externo (PostgreSQL, MySQL)
- As configurações de CPU e memória podem ser ajustadas conforme necessário
- O HPA requer metrics-server instalado no cluster

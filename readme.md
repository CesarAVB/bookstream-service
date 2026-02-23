# 📚 BookStream Service

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![AWS SDK](https://img.shields.io/badge/AWS%20SDK-2.20.26-FF9900.svg)](https://aws.amazon.com/sdk-for-java/)
[![MinIO](https://img.shields.io/badge/MinIO-Compatible-red.svg)](https://min.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Microsserviço para gerenciamento e streaming de conteúdo digital. Suporta livros em formato **PDF**, **EPUB** e **Audiobook**, com armazenamento de arquivos e capas no **MinIO** (compatível com S3), geração de links temporários e streaming de áudio.

---

## 📋 Sumário

- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Configuração](#-configuração)
- [Endpoints](#-endpoints)
- [Fluxo de Cadastro](#-fluxo-de-cadastro)
- [Como Executar](#-como-executar)

---

## ✅ Funcionalidades

- Cadastro de livros com metadados (nome, autor, gênero, ISBN, ano)
- Upload de arquivos PDF, EPUB e Audiobook para o MinIO
- Upload de imagem de capa
- Geração de links temporários (presigned URLs) para leitura
- Streaming de áudio via redirect para URL presigned
- Listagem com filtros combinados e paginação
- Atualização parcial de metadados
- Gerenciamento de status (ATIVO, INATIVO, PROCESSANDO)
- Remoção de livro com exclusão dos arquivos no MinIO

---

## 🛠 Tecnologias

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Java | 21 | Linguagem |
| Spring Boot | 3.x | Framework principal |
| Spring Data JPA | - | Persistência |
| MySQL | 8.0 | Banco de dados |
| AWS SDK v2 (S3) | 2.20.26 | Integração com MinIO |
| MinIO | - | Armazenamento de arquivos |
| Lombok | - | Redução de boilerplate |
| Bean Validation | - | Validação de requests |

---

## 📁 Estrutura do Projeto

```
src/main/java/br/com/sistema/bookstream/
├── config/
│   └── MinioConfig.java           # Configuração dos beans S3Client e S3Presigner
├── controller/
│   └── LivroController.java       # Endpoints REST
├── dto/
│   ├── request/
│   │   ├── LivroCadastroRequest.java
│   │   ├── LivroAtualizacaoRequest.java
│   │   └── LivroFiltroRequest.java
│   └── response/
│       ├── LivroResponse.java
│       ├── LivroResumoResponse.java
│       ├── LivroUploadResponse.java
│       └── LivroLinkTemporarioResponse.java
├── entity/
│   ├── Livro.java
│   └── enums/
│       ├── TipoLivro.java         # PDF, EPUB, AUDIOBOOK
│       └── StatusLivro.java       # ATIVO, INATIVO, PROCESSANDO
├── repository/
│   └── LivroRepository.java
└── service/
    ├── LivroService.java          # Regras de negócio
    └── MinioService.java          # Operações com MinIO via AWS SDK v2
```

---

## ⚙️ Configuração

### Variáveis de Ambiente

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `MINIO_ENDPOINT` | URL do servidor MinIO | `http://localhost:9000` |
| `MINIO_ACCESSKEY` | Access key do MinIO | `minioadmin` |
| `MINIO_SECRETKEY` | Secret key do MinIO | `minioadmin` |
| `MINIO_REGION` | Região configurada | `us-east-1` |
| `MINIO_BUCKETNAME` | Nome do bucket | `bookstream` |
| `MINIO_LINK_EXPIRACAO_MINUTOS` | Expiração dos links temporários | `60` |

### application.properties

```properties
# Banco de dados
spring.datasource.url=jdbc:mysql://localhost:3306/bookstream
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# MinIO / S3
minio.endpoint=${MINIO_ENDPOINT}
minio.access-key=${MINIO_ACCESSKEY}
minio.secret-key=${MINIO_SECRETKEY}
minio.region=${MINIO_REGION}
minio.bucket.nome=${MINIO_BUCKETNAME}
minio.link.expiracao-minutos=${MINIO_LINK_EXPIRACAO_MINUTOS:60}
```

---

## 🔌 Endpoints

### Livros

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/livros` | Cadastra metadados do livro |
| `GET` | `/api/v1/livros` | Lista livros com filtros e paginação |
| `GET` | `/api/v1/livros/{id}` | Busca detalhes de um livro |
| `PUT` | `/api/v1/livros/{id}` | Atualiza metadados do livro |
| `PATCH` | `/api/v1/livros/{id}/status` | Atualiza status do livro |
| `DELETE` | `/api/v1/livros/{id}` | Remove livro e arquivos do MinIO |

### Upload

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/livros/{id}/arquivo` | Upload do arquivo principal (PDF/EPUB/Audiobook) |
| `POST` | `/api/v1/livros/{id}/capa` | Upload da imagem de capa |

### Acesso ao Conteúdo

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/v1/livros/{id}/link` | Gera link temporário para PDF ou EPUB |
| `GET` | `/api/v1/livros/{id}/capa/link` | Gera link temporário para a capa |
| `GET` | `/api/v1/livros/{id}/streaming` | Gera link de streaming para Audiobook |

### Parâmetros de Filtro (GET /api/v1/livros)

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `nome` | `String` | Filtro parcial por nome |
| `autor` | `String` | Filtro parcial por autor |
| `genero` | `String` | Filtro parcial por gênero |
| `tipo` | `TipoLivro` | `PDF`, `EPUB` ou `AUDIOBOOK` |
| `status` | `StatusLivro` | `ATIVO`, `INATIVO` ou `PROCESSANDO` |
| `anoDe` | `Integer` | Ano mínimo de publicação |
| `anoAte` | `Integer` | Ano máximo de publicação |
| `page` | `Integer` | Número da página (padrão: 0) |
| `size` | `Integer` | Itens por página (padrão: 20) |
| `sort` | `String` | Campo de ordenação (padrão: nome) |

---

## 🔄 Fluxo de Cadastro

O cadastro de um livro é feito em etapas separadas:

```
1. POST /api/v1/livros          → cadastra metadados (status: PROCESSANDO)
2. POST /api/v1/livros/{id}/arquivo → faz upload do arquivo (status: ATIVO)
3. POST /api/v1/livros/{id}/capa    → faz upload da capa (opcional)
```

Essa separação permite validar os metadados antes de aceitar o arquivo, e oferece flexibilidade para reenviar o arquivo sem recadastrar o livro.

---

## ▶️ Como Executar

### Pré-requisitos

- Java 21+
- Maven 3.8+
- MySQL 8.0 rodando
- MinIO rodando (ou bucket S3 configurado)

### Passos

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/bookstream-service.git
cd bookstream-service

# Configure as variáveis de ambiente ou edite o application.properties

# Execute
./mvnw spring-boot:run
```

### MinIO com Docker (para desenvolvimento)

```bash
docker run -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  quay.io/minio/minio server /data --console-address ":9001"
```

Acesse o console do MinIO em: `http://localhost:9001`

---

## 📄 Licença

Este projeto está sob a licença [MIT](https://opensource.org/licenses/MIT).
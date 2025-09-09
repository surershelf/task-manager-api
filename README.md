# TASK-MANAGER-API

*Empower Productivity, Simplify Success, Accelerate Results*

![Last Commit](https://img.shields.io/badge/last%20commit-today-brightgreen)
![Java](https://img.shields.io/badge/Java-98.5%25-orange)
![Languages](https://img.shields.io/badge/languages-2-blue)

*Built with the tools and technologies:*

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=flat&logo=docker&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=flat&logo=mysql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=black)

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)

## Overview

Task Manager API é uma solução robusta para gerenciamento de tarefas e atividades, construída com Spring Boot. A API oferece funcionalidades completas de CRUD para usuários, atividades e progresso, com autenticação segura e documentação interativa via Swagger.

### Principais Funcionalidades

- **Autenticação de Usuários**: Registro, login e recuperação de senha
- **Gerenciamento de Atividades**: CRUD completo com diferentes frequências (diária, semanal, mensal)
- **Controle de Progresso**: Marcação e acompanhamento do progresso das atividades
- **API RESTful**: Endpoints bem estruturados seguindo as melhores práticas
- **Documentação Interativa**: Swagger UI para facilitar testes e integração
- **Containerização**: Deploy facilitado com Docker

## Getting Started

### Prerequisites

Este projeto requer as seguintes dependências:

- **Programming Language**: Java 17+
- **Package Manager**: Maven 3.6+
- **Container Runtime**: Docker
- **Database**: MySQL 8.0+ ou H2 (para desenvolvimento)

### Installation

Construa a task-manager-api a partir do código fonte e instale as dependências:

1. **Clone the repository**:

```bash
git clone https://github.com/surershelf/task-manager-api
```

2. **Navigate to the project directory**:

```bash
cd task-manager-api
```

3. **Install the dependencies**:

**Using docker**:

```bash
docker build -t surershelf/task-manager-api .
```

**Using maven**:

```bash
mvn install
```

## Usage

Execute o projeto com:

**Using docker**:

```bash
docker run -p 8080:8080 surershelf/task-manager-api
```

**Using maven**:

```bash
mvn spring-boot:run
```

### Configuration

Configure as variáveis de ambiente no arquivo `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# Server Configuration
server.port=8080
```

## API Documentation

A API está documentada usando OpenAPI 3.0 e Swagger UI. Após iniciar a aplicação, acesse:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`

### Principais Endpoints

#### Autenticação
- `POST /api/auth/register` - Registrar novo usuário
- `POST /api/auth/login` - Fazer login
- `POST /api/auth/forgot-password` - Recuperar senha

#### Usuários
- `GET /api/user/{userId}/profile` - Ver perfil do usuário
- `PUT /api/user/{userId}/profile` - Atualizar perfil
- `PUT /api/user/{userId}/password` - Alterar senha

#### Atividades
- `GET /api/activities/usuario/{usuarioId}` - Listar atividades
- `POST /api/activities/usuario/{usuarioId}` - Criar atividade
- `PUT /api/activities/{atividadeId}/usuario/{usuarioId}` - Atualizar atividade
- `DELETE /api/activities/{atividadeId}/usuario/{usuarioId}` - Excluir atividade

#### Progresso
- `POST /api/progress/create` - Marcar progresso
- `GET /api/progress/user/{userId}` - Ver progresso do usuário
- `GET /api/progress/user/{userId}/stats` - Estatísticas de progresso

## Testing

Task-manager-api usa **JUnit** como framework de testes. Execute a suíte de testes com:

```bash
mvn test
```

Para executar testes com cobertura:

```bash
mvn test jacoco:report
```

## Deployment

### Docker Compose

Para deploy completo com MySQL:

```bash
docker-compose up -d
```

### Railway

Para deploy na Railway:

1. Conecte seu repositório GitHub
2. Configure as variáveis de ambiente
3. Deploy automático será executado

### AWS Elastic Beanstalk

1. Crie um arquivo `Dockerrun.aws.json`
2. Configure RDS MySQL
3. Faça upload do arquivo ZIP

## Database Schema

A aplicação utiliza as seguintes entidades principais:

- **User**: Informações do usuário e autenticação
- **Activity**: Atividades com frequência e status
- **Progress**: Registros de progresso das atividades

## Contributing

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## License

Distribuído sob a MIT License. Veja `LICENSE` para mais informações.

## Contact

Seu Nome - [seu.email@exemplo.com](mailto:seu.email@exemplo.com)

Link do Projeto: [https://github.com/surershelf/task-manager-api](https://github.com/surershelf/task-manager-api)

---

⭐ Não esqueça de dar uma estrela no projeto se ele foi útil para você!

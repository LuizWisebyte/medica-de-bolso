# Medica de Bolso

Plataforma de telemedicina que permite consultas médicas remotas, prescrições digitais e comunicação entre médicos e pacientes.

## Funcionalidades

- Autenticação e autorização de usuários (pacientes e médicos)
- Consultas médicas remotas via chat, áudio e vídeo
- Prescrições e atestados digitais
- Prontuário eletrônico
- Sistema de pagamento integrado
- Notificações em tempo real

## Tecnologias

### Backend
- Java 17
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- WebSocket
- Apache Kafka
- PostgreSQL
- MongoDB
- Redis

### Frontend (Mobile)
- Flutter

## Requisitos

- Java 17
- Maven
- PostgreSQL
- MongoDB
- Redis
- Apache Kafka

## Configuração do Ambiente

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/medicadebolso.git
cd medicadebolso
```

2. Configure as variáveis de ambiente:
```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

3. Edite o arquivo `application.yml` com suas configurações locais.

4. Inicie os serviços necessários:
```bash
docker-compose up -d
```

5. Execute a aplicação:
```bash
mvn spring-boot:run
```

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── br/com/medicadebolso/
│   │       ├── domain/
│   │       │   ├── config/       # Configurações do Spring
│   │       │   ├── controller/   # Controladores REST
│   │       │   ├── dto/          # Objetos de transferência de dados
│   │       │   ├── model/        # Entidades do domínio
│   │       │   ├── repository/   # Repositórios JPA
│   │       │   └── service/      # Serviços de negócio
│   │       └── MedicaDeBolsoApplication.java
│   └── resources/
│       └── application.yml
```

## API Endpoints

### Autenticação
- `POST /api/auth/registro` - Registro de novo usuário
- `POST /api/auth/login` - Login de usuário existente

### Pacientes
- `GET /api/pacientes/perfil` - Obter perfil do paciente
- `PUT /api/pacientes/perfil` - Atualizar perfil do paciente
- `GET /api/pacientes/consultas` - Listar consultas do paciente
- `POST /api/pacientes/consultas` - Agendar nova consulta

### Médicos
- `GET /api/medicos/perfil` - Obter perfil do médico
- `PUT /api/medicos/perfil` - Atualizar perfil do médico
- `GET /api/medicos/consultas` - Listar consultas do médico
- `POST /api/medicos/prescricoes` - Emitir prescrição

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes. 
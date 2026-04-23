# My Bank App

Микросервисное банковское приложение на Java 21, Spring Boot 3.3.4, Spring Cloud 2023.0.3.

## Сервисы

| Сервис                | Порт |
|-----------------------|------|
| eureka-server         | 8761 |
| config-server         | 8888 |
| auth-server           | 9000 |
| gateway-server        | 8765 |
| accounts-service      | 8081 |
| cash-service          | 8082 |
| transfer-service      | 8083 |
| notifications-service | 8084 |
| front-app             | 8080 |

## Запуск

### Docker Compose

```bash
mvn clean package -DskipTests
docker-compose up --build
```

Открыть: http://localhost:8080

### Локально

```bash
# PostgreSQL
docker run -d --name bankdb \
  -e POSTGRES_DB=bankdb \
  -e POSTGRES_USER=bankuser \
  -e POSTGRES_PASSWORD=bankpassword \
  -p 5432:5432 postgres:16-alpine

# Сборка
mvn clean package -DskipTests

# Запуск в порядке:
java -jar eureka-server/target/*.jar
java -jar config-server/target/*.jar
java -jar auth-server/target/*.jar
java -jar gateway-server/target/*.jar
java -jar accounts-service/target/*.jar
java -jar cash-service/target/*.jar
java -jar transfer-service/target/*.jar
java -jar notifications-service/target/*.jar
java -jar front-app/target/*.jar
```

## Тестовые пользователи

| Логин | Пароль   |
|-------|----------|
| user1 | password |
| user2 | password |

## Тесты

```bash
mvn test
```
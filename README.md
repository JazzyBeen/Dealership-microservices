# Dealership Microservices
<p align="center">
  <img src="https://github.com/user-attachments/assets/fb3e85c7-4beb-4fe4-ab6f-e95b4239b16a">
</p>

![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-brightgreen?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)
![Kafka](https://img.shields.io/badge/Apache_Kafka-3.6-black?logo=apachekafka)
![gRPC](https://img.shields.io/badge/gRPC-1.62-4285F4?logo=google)
![Keycloak](https://img.shields.io/badge/Keycloak-24.0-blue?logo=keycloak)

Многомодульная микросервисная система для управления процессами современного автосалона (на примере Tesla). Проект демонстрирует применение паттернов распределенных систем, чистой архитектуры (Clean Architecture) и микросервисного взаимодействия.

---

## Архитектура

Система разделена на два независимых микросервиса, каждый из которых имеет собственную базу данных:

1. **Order Service (`:8080`)** — отвечает за работу с клиентами, оформление заказов (автомобили в наличии и кастомные сборки) и запись на тест-драйвы.
2. **Storage Service (`:8082`)** — отвечает за складской учет, каталог автомобилей, запчасти и внутренние процессы сборки.

### Взаимодействие между сервисами
* **Синхронное (gRPC):** Для мгновенного получения актуального каталога автомобилей клиент (OrderService) отправляет gRPC-запрос на сервер (StorageService). Реализован паттерн отказоустойчивости (Timeout/Deadline) — при падении склада возвращается контролируемая HTTP 503 ошибка.
* **Асинхронное (Kafka):** Бизнес-процессы оплаты и сборки реализованы на базе паттерна **Choreography Saga**. Для гарантии доставки сообщений (Eventual Consistency) в обоих сервисах применен **Outbox Pattern** с фоновыми планировщиками (Schedulers).

---

## Безопасность (Security)
Аутентификация и авторизация вынесены на сторону **Keycloak** (OAuth 2.0 / OpenID Connect).
* JWT-токены передаются в заголовке `Authorization: Bearer`.
* **Ролевая модель:** Разграничение доступа между ролями `USER`, `MANAGER`, `WAREHOUSE_ADMIN`, `ADMIN`.
* **Owner Check:** Реализованы проверки на уровне SpEL-выражений (`@PreAuthorize("@securityLogic.isOrderOwner(#id, #jwt)")`), запрещающие пользователям просматривать или отменять чужие заказы.

---

## Технологический стек
* **Core:** Java 21, Spring Boot 3 (Web, Data JPA, Security).
* **Databases:** PostgreSQL (раздельные логические БД для каждого сервиса), Liquibase (для миграций).
* **Integration:** Apache Kafka, gRPC, Protocol Buffers (Protobuf).
* **Testing:** JUnit 5, Testcontainers (PostgreSQL), Spring Boot Test, MockMvc.
* **Tools:** MapStruct, Lombok, OpenAPI/Swagger.
* **Infrastructure:** Docker, Docker Compose.

---

## Как запустить проект

### 1. Запуск инфраструктуры
Перейдите в папку `docker` и поднимите базы данных, Kafka, Zookeeper и Keycloak:
```bash
cd docker
docker-compose up -d

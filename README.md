# Broker

## О проекте

Демонстрация событийной архитектуры на Kafka. Сервис **worker** каждые несколько секунд берёт случайный товар из БД и публикует в топик `product-updates` событие с новой ценой и остатком. Сервис **store** (REST API магазина) потребляет эти события, обновляет товар в PostgreSQL и сразу рассылает изменение подписчикам по **WebSocket** и **SSE**; встроенная веб-страница показывает обновления в реальном времени.

## Стек

| Компонент | Технологии |
|---|---|
| Общее | Java 21, Spring Boot 4.1.1, Gradle (wrapper 9.8.1), Lombok |
| store | Spring Web MVC, Spring Data JPA, Flyway, Spring Kafka (consumer), WebSocket, SSE, Spring Data Redis (кэш изображений, TTL 10 мин), MapStruct 1.6.3, springdoc-openapi 3.1.1, Testcontainers |
| worker | Spring Kafka (producer, `acks=all`, идемпотентность), Spring Data JPA, `@Scheduled` |
| Инфраструктура | PostgreSQL 18, Redis 7, Apache Kafka 4.1.2 (KRaft, один узел), Kafbat Kafka UI, Docker Compose |

## Быстрый старт

```bash
docker compose up --build
```

Порядок запуска (healthcheck'и): postgres, redis, kafka → store → worker. Worker стартует после store, потому что таблицы создаёт Flyway в store.

| Что | Адрес |
|---|---|
| Веб-интерфейс магазина (товары + realtime) | http://localhost:8080/ |
| Swagger UI | http://localhost:8080/swagger/index.html |
| OpenAPI JSON | http://localhost:8080/api-docs |
| WebSocket | `ws://localhost:8080/ws` |
| SSE | http://localhost:8080/api/v1/sse/subscribe?clientId=<любой id> |
| Kafka UI | http://localhost:8081 |
| PostgreSQL | `127.0.0.1:5432` (admin / admin, БД `store`) |
| Redis | `127.0.0.1:6379` |
| Kafka с хоста | `localhost:9092` |

Остановка: `docker compose down` (с удалением данных: `docker compose down -v`).

## Проверка функционала

1. **Realtime.** Откройте http://localhost:8080/ — статусы WebSocket и SSE должны стать «Подключён», примерно раз в 3 с приходят обновления товаров. То же в консоли:

```bash
curl -N "http://localhost:8080/api/v1/sse/subscribe?clientId=demo"
```

2. **Сообщения в Kafka.** В Kafka UI (http://localhost:8081) откройте топик `product-updates` → Messages. Логи цепочки:

```bash
docker compose logs -f worker    # "Событие отправлено: eventId=..., offset=..."
docker compose logs -f store
```

3. **REST API** (авторизации нет):

| Группа | Маршруты |
|---|---|
| `/api/v1/clients` | `GET`, `GET /{id}`, `GET /search`, `POST`, `DELETE /{id}`, `PATCH /{id}/address` |
| `/api/v1/suppliers` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `/api/v1/products` | `GET`, `GET /{id}`, `POST`, `DELETE /{id}`, `PATCH /{id}/stock` |
| `/api/v1/images` | `GET /product/{productId}`, `GET /{id}`, `POST` (Base64), `PUT /{id}`, `DELETE /{id}` |
| `/api/v1/sse` | `GET /subscribe?clientId=` |

Схемы запросов — в Swagger UI. Тестовые данные (клиенты, поставщики, товары) создаёт миграция `V2`.

## Структура проекта

```
.
├── docker-compose.yml     # postgres, redis, kafka, store, worker, kafka-ui
├── .env.example
└── src
    ├── store              # REST + Kafka consumer + WebSocket/SSE + статический UI
    │   └── src/main/java/com/shastage/store
    │       ├── web             # контроллеры, DTO, web-мапперы
    │       ├── domain          # модели, команды, сервисы, интерфейсы репозиториев
    │       ├── datasource      # JPA-сущности, адаптеры репозиториев
    │       ├── infrastructure  # KafkaConsumer, WebSocketHandler, SSE
    │       └── config          # Redis-кэш, WebSocket, OpenAPI
    └── worker             # генератор событий (@Scheduled) + Kafka producer
```

store построен по слоям с портами/адаптерами: домен не зависит от JPA. Поток данных: `worker → Kafka (product-updates) → store → PostgreSQL → WebSocket/SSE → браузер`.

## Особенности и ограничения

- Данные событий случайные (цена до 999.99, остаток 0–49); это демонстрация, а не реальная бизнес-логика.
- Топик `product-updates` создаётся worker'ом (1 партиция, 1 реплика), Kafka — один узел без отказоустойчивости.
- Kafka доступна с хоста на `localhost:9092`, внутри Docker — `kafka:19092`; для запуска приложений вне Docker поменяйте `KAFKA_BOOTSTRAP_SERVERS`.
- Redis кэширует только изображения; при недоступности Redis ошибки кэша логируются, а запросы продолжают работать.
- REST API без аутентификации, WebSocket принимает любые Origin (`*`) — только для демонстрации.
- В `src/store/package.json` объявлен фронтенд-тест на Playwright, но файла `tests/frontend.test.mjs` в репозитории нет.
- `gradle-wrapper.jar` в Git LFS: без `git lfs pull` сборка не стартует. Образ Kafka UI использует тег `latest`.
- Docker-образы в этой сессии не собирались (нет доступа к реестрам) — проверена только валидность compose (`docker compose config`).

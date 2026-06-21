# neurotools-backend

Kotlin Spring Boot backend for NeuroTools.

## Stack

- Kotlin, Maven, Spring Boot
- PostgreSQL
- Hibernate/JPA
- Liquibase migrations
- Optional S3-compatible storage configuration, usable with MinIO locally

## Local services

```bash
docker compose up -d postgres minio
```

## Run

Maven is required locally.

```bash
mvn spring-boot:run
```

API starts on `http://localhost:8080`.

## Endpoints

- `GET /api/tools`
- `GET /api/tools/{slug}`
- `POST /api/tools`
- `PUT /api/tools/{id}`
- `DELETE /api/tools/{id}`

## S3/MinIO

Set `S3_ENABLED=true` and provide credentials when object storage is needed.
For local MinIO:

```bash
S3_ENABLED=true \
S3_ENDPOINT=http://localhost:9000 \
S3_ACCESS_KEY=neurotools \
S3_SECRET_KEY=neurotools-secret \
mvn spring-boot:run
```

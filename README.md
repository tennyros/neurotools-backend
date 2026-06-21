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
cp .env.example .env
```

```bash
docker compose up -d postgres minio
```

## Run

```bash
mvn spring-boot:run
```

API starts on `http://localhost:8080`.

The app reads database, admin auth, and S3 settings from environment variables. If you use the local services above, set the values from `.env` in your shell before starting the app.

## Endpoints

- `GET /api/tools`
- `GET /api/tools/{slug}`
- `POST /api/tools/{slug}/click`
- `GET /api/tools/summary`
- `POST /api/admin/sync`
- `POST /api/admin/sync/{category}`
- `GET /api/admin/sync/status`

## CI/CD

- GitHub Actions runs `mvn -B test` on pull requests and pushes.
- Main branch pushes trigger the Render deploy hook when `RENDER_DEPLOY_HOOK_URL` is set in GitHub secrets.
- `render.yaml` and `Dockerfile` define the production service.

## Security

- `GET /api/tools/**` stays public.
- `POST /api/tools/*/click` stays public for affiliate tracking.
- `/api/admin/**` requires HTTP Basic auth with `ADMIN_USERNAME` and `ADMIN_PASSWORD`.
- Set those values in your deployment environment; do not commit them.
- Click tracking requires a signed `CLICK_TOKEN_SECRET` from the API response and is also rate limited per client and slug using `CLICK_RATE_LIMIT_MAX_ATTEMPTS` and `CLICK_RATE_LIMIT_WINDOW_SECONDS`.

## S3/MinIO

Set `S3_ENABLED=true` and provide credentials through environment variables when object storage is needed.
For local MinIO, create a `.env` file from `.env.example` and set:

```bash
S3_ENABLED=true
S3_ENDPOINT=http://localhost:9000
S3_ACCESS_KEY=<your-minio-access-key>
S3_SECRET_KEY=<your-minio-secret-key>
```

# sarvasya-lms-backend

Spring Boot (Java 17) backend with multi-tenancy, JWT auth, and Flyway migrations.

## Setup

- **Prereqs**: Java 17, PostgreSQL, Maven (or use `./mvnw`)
- **Config**: copy `.env.example` → `.env` and set required values.
  - **Required**: `JWT_SECRET` (base64), `SIGNUP_KEY`
  - **DB**: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`

## Run

```bash
./mvnw spring-boot:run
```

## Test

```bash
./mvnw test
```

## Notes

- **Secrets**: do not commit `.env`. The app requires `JWT_SECRET` and `SIGNUP_KEY` at runtime.
- **Rate limiting**: auth endpoints are rate-limited via `security.rate-limit.auth.requests-per-minute`.
- **Docs**:
  - API reference: `docs/API_DOCUMENTATION.md`
  - Legacy Spring Boot help: `docs/HELP.md`


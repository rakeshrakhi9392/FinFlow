# Deployment Guide

Production deployment for the **Employee Reimbursement System (ERS)** — Spring Boot API on **Google Cloud Run**, database on **Neon PostgreSQL**, optional Docker Compose for local parity.

**Author:** Madasu Rakesh

---

## Architecture at a glance

```text
┌─────────────────┐     HTTPS / cookies      ┌──────────────────────────┐
│  React Frontend │ ───────────────────────► │  Cloud Run (ERSBackend)  │
│  (CDN / Nginx)  │ ◄─────────────────────── │  Spring Boot 3.2 + Actuator│
└─────────────────┘                          └────────────┬─────────────┘
                                                          │ JDBC + SSL
                                                          ▼
                                                 ┌─────────────────┐
                                                 │ Neon PostgreSQL │
                                                 └─────────────────┘
```

See [ARCHITECTURE.md](ARCHITECTURE.md) for the full system diagram and module map.

---

## Prerequisites

| Tool | Purpose |
|------|---------|
| JDK 17+, Maven 3.9+ | Local backend builds |
| Node.js 20+, npm | Local frontend builds |
| Docker + Docker Compose | Containerized stack |
| Neon account | Managed PostgreSQL |
| Google Cloud project | Cloud Run + Artifact Registry |
| `gcloud` CLI (optional) | Manual deploy / debugging |
| GitHub repository secrets | CI/CD |

---

## 1. Neon PostgreSQL

1. Create a project at [https://console.neon.tech](https://console.neon.tech).
2. Create a database (e.g. `neondb`) and copy the connection string.
3. Convert the Neon URI to a JDBC URL:

```text
# Neon connection string (example)
postgresql://USER:PASSWORD@ep-xxxx.region.aws.neon.tech/neondb?sslmode=require

# Equivalent Spring datasource URL
jdbc:postgresql://ep-xxxx.region.aws.neon.tech/neondb?sslmode=require
```

4. Set environment variables (never commit secrets):

| Variable | Example |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://ep-….neon.tech/neondb?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | `neondb_owner` |
| `SPRING_DATASOURCE_PASSWORD` | *(from Neon console)* |
| `SPRING_JPA_DEFAULT_SCHEMA` | `public` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` (demo) or `validate` (after migrations) |

> **Note:** Production demos use `ddl-auto=update`. For stricter production, introduce Flyway/Liquibase and switch to `validate`.

---

## 2. Local Docker Compose

Copy environment template:

```bash
cp .env.example .env
```

### Option A — Local Postgres

```bash
docker compose --profile local-db up --build
```

- API: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- Metrics: [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics) *(requires ADMIN/FINANCE session)*
- Frontend: [http://localhost:8081](http://localhost:8081)

### Option B — Neon from Docker

Set `SPRING_DATASOURCE_*` in `.env` to your Neon credentials, then:

```bash
docker compose up --build
```

---

## 3. Manual local run (without Docker)

**Backend**

```bash
cd ERSBackend
# Optional: export SPRING_DATASOURCE_URL / USERNAME / PASSWORD for Neon
./mvnw spring-boot:run
```

**Frontend**

```bash
cd ers-frontend
npm install
set REACT_APP_API_BASE_URL=http://localhost:8080   # Windows PowerShell: $env:REACT_APP_API_BASE_URL=...
npm start
```

---

## 4. Google Cloud Run (backend)

### One-time GCP setup

```bash
gcloud config set project YOUR_PROJECT_ID
gcloud services enable run.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com

# Service account for GitHub Actions (example roles)
# - roles/run.admin
# - roles/artifactregistry.writer
# - roles/iam.serviceAccountUser
```

Create a JSON key for the service account and store it as the GitHub secret `GCP_SA_KEY`.

### GitHub secrets & variables

| Name | Type | Description |
|------|------|-------------|
| `GCP_SA_KEY` | Secret | Service account JSON key |
| `GCP_PROJECT_ID` | Secret | GCP project id |
| `NEON_JDBC_URL` | Secret | `jdbc:postgresql://…?sslmode=require` |
| `NEON_DB_USER` | Secret | Neon username |
| `NEON_DB_PASSWORD` | Secret | Neon password |
| `FRONTEND_ORIGIN` | Secret | Allowed CORS origin(s), e.g. `https://app.example.com` |
| `BACKEND_PUBLIC_URL` | Secret | Cloud Run URL (after first deploy), e.g. `https://ers-backend-….run.app` |
| `GCP_REGION` | Variable | Optional; default `us-central1` |

### Deploy

Push to `main` (paths under `ERSBackend/**`) or run **Actions → Deploy Backend to Google Cloud Run → Run workflow**.

The workflow:

1. Runs Maven tests  
2. Builds the backend Docker image  
3. Pushes to Artifact Registry  
4. Deploys to Cloud Run with `--session-affinity` (required for in-memory HTTP sessions)

### Verify

```text
https://YOUR-SERVICE.run.app/actuator/health
https://YOUR-SERVICE.run.app/swagger-ui.html
https://YOUR-SERVICE.run.app/v3/api-docs
https://YOUR-SERVICE.run.app/actuator/metrics   # authenticated
```

---

## 5. Frontend against Cloud Run

Build with the Cloud Run API URL:

```bash
cd ers-frontend
npm ci
REACT_APP_API_BASE_URL=https://YOUR-SERVICE.run.app npm run build
```

Serve `build/` via Nginx, Firebase Hosting, Cloud Storage + CDN, or the provided frontend Dockerfile:

```bash
docker build -t ers-frontend \
  --build-arg REACT_APP_API_BASE_URL=https://YOUR-SERVICE.run.app \
  ./ers-frontend
```

Ensure `FRONTEND_ORIGIN` / `ERS_CORS_ALLOWED_ORIGINS` matches the frontend origin exactly. Cross-origin session cookies require:

- `SERVER_SERVLET_SESSION_COOKIE_SECURE=true`
- `SERVER_SERVLET_SESSION_COOKIE_SAME_SITE=none`
- Frontend Axios `withCredentials: true` (already configured)

---

## 6. Observability endpoints

| Endpoint | Auth | Purpose |
|----------|------|---------|
| `GET /actuator/health` | Public | Liveness / readiness for Cloud Run & Docker |
| `GET /actuator/health/liveness` | Public | Kubernetes-style liveness |
| `GET /actuator/health/readiness` | Public | Kubernetes-style readiness |
| `GET /actuator/info` | Public | App metadata |
| `GET /actuator/metrics` | ADMIN / FINANCE | JVM & app metrics |
| `GET /actuator/metrics/{name}` | ADMIN / FINANCE | Named metric |
| `GET /swagger-ui.html` | Public | Interactive API docs |
| `GET /v3/api-docs` | Public | OpenAPI 3 JSON |

---

## 7. Environment reference

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | `prod` on Cloud Run / Docker | — |
| `PORT` | HTTP port (Cloud Run sets this) | `8080` |
| `SPRING_DATASOURCE_URL` | JDBC URL (Neon or Postgres) | local Postgres |
| `SPRING_DATASOURCE_USERNAME` | DB user | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `password` |
| `SPRING_JPA_DEFAULT_SCHEMA` | Hibernate schema | `reimbursement_schema` (local) / `public` (prod) |
| `ERS_CORS_ALLOWED_ORIGINS` | Comma-separated origins | `http://localhost:3000,http://localhost:8081` |
| `ERS_API_SERVER_URL` | OpenAPI server URL | `http://localhost:8080` |
| `ERS_VENDOR_PROVIDER` | `mock-sap` \| `sap` \| `oracle` \| `exchange-rate` | `mock-sap` |
| `REACT_APP_API_BASE_URL` | Frontend API base (build-time) | `http://localhost:8080` |

---

## 8. Security notes for production

- Prefer Google Secret Manager for Neon credentials instead of plain Cloud Run env vars when hardening further.
- Session auth needs sticky sessions (`--session-affinity`) or an external session store (Redis) for multi-instance scale-out.
- Restrict Swagger in locked-down environments via `SPRINGDOC_SWAGGER_UI_ENABLED=false`.
- Keep CSRF considerations in mind if you move beyond same-site cookie patterns; current API uses cookie sessions with CSRF disabled for SPA convenience.
- Rotate Neon and GCP credentials regularly; never commit `.env`.

---

## 9. Rollback

```bash
gcloud run services update-traffic ers-backend \
  --region=us-central1 \
  --to-revisions=PREVIOUS_REVISION=100
```

Or redeploy a prior image tag from Artifact Registry.

---

## Related documents

- [README.md](README.md) — product overview & local quick start  
- [ARCHITECTURE.md](ARCHITECTURE.md) — system & module diagram  
- [WORKFLOW.md](WORKFLOW.md) — approval lifecycle  
- [INTEGRATION.md](INTEGRATION.md) — vendor / ERP adapters  

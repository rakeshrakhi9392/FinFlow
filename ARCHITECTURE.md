# Architecture

System architecture for the **Employee Reimbursement System (ERS)** by **Madasu Rakesh**.

---

## High-level system

```mermaid
flowchart LR
  subgraph Clients
    FE[React SPA<br/>ers-frontend]
    SW[Swagger UI]
  end

  subgraph Cloud["Google Cloud Run"]
    API[ERSBackend<br/>Spring Boot 3.2]
    ACT[Actuator<br/>/health /metrics]
    DOC[springdoc OpenAPI]
  end

  subgraph Data
    NEON[(Neon PostgreSQL)]
  end

  subgraph External
    VENDOR[Vendor / ERP<br/>Mock SAP · SAP · Oracle]
  end

  FE -->|HTTPS + JSESSIONID| API
  SW --> DOC
  DOC --> API
  ACT --> API
  API -->|JDBC SSL| NEON
  API -->|VendorIntegrationService| VENDOR
```

---

## Backend module map

```mermaid
flowchart TB
  CTRL[Controllers<br/>Users · Reimbursements · Budgets<br/>Workflow · Vendor · Departments]

  SEC[Security<br/>SessionAuth · RBAC · CORS]
  WF[Workflow<br/>ReimbursementWorkflow]
  BUD[Budget Engine<br/>EscalationPolicyEngine<br/>BudgetPolicyEngine]
  FIN[FinanceService]
  VEN[Vendor Integration<br/>Resilient decorator<br/>+ adapters]

  JPA[(JPA Entities / Repositories)]
  DB[(PostgreSQL / Neon)]

  CTRL --> SEC
  CTRL --> WF
  CTRL --> BUD
  CTRL --> FIN
  FIN --> VEN
  WF --> JPA
  BUD --> JPA
  FIN --> JPA
  JPA --> DB
```

---

## Request lifecycle (reimbursement)

```mermaid
sequenceDiagram
  participant E as Employee UI
  participant API as Cloud Run API
  participant WF as Workflow Engine
  participant BE as Budget Engine
  participant F as Finance Service
  participant V as Vendor Adapter
  participant DB as Neon DB

  E->>API: POST /users/login
  API->>DB: verify BCrypt password
  API-->>E: Set-Cookie JSESSIONID

  E->>API: POST /api/reimbursements
  API->>BE: evaluate escalation
  API->>WF: route to MANAGER_REVIEW (+ flags)
  API->>DB: persist claim + history
  API-->>E: claim DTO + timeline

  Note over API,WF: Manager → Senior? → Finance

  API->>F: finance approve
  F->>DB: apply budget spend
  F->>V: postReimbursement (timeout + retry)
  alt ERP success
    V-->>F: accounting document
    F->>DB: VENDOR_PROCESSING
  else ERP failure
    F->>DB: FAILED_VENDOR_SYNC
  end
```

---

## Authentication

| Concern | Implementation |
|---------|----------------|
| Credential store | PostgreSQL `users` table; passwords hashed with **BCrypt** |
| Login | `POST /users/login` → validates credentials → `SessionAuthService.establishSession` |
| Session | Server-side `HttpSession` (`JSESSIONID` cookie) + Spring `SecurityContext` |
| Frontend | Axios `withCredentials: true`; UI mirror in `sessionStorage` |
| Authorization | Spring Security method/URL RBAC: `employee`, `manager`, `senior_manager`, `finance`, `admin` |
| Logout | `POST /users/logout` invalidates session |
| Production cookies | `Secure` + `SameSite=None` for cross-origin SPA; Cloud Run **session affinity** |

Unauthenticated API calls receive JSON `401`. Insufficient role receives JSON `403`.

---

## Workflow

Multi-stage state machine documented in [WORKFLOW.md](WORKFLOW.md):

```text
Submitted → Manager Review → (Senior Manager) → Finance Review
         → Pending Vendor Confirmation → Vendor Processing → Paid
```

- Escalation flags set at submit (`requiresSeniorReview`, amount/budget reasons).
- Every transition appends `approval_history`.
- Admin-configurable rules via `PUT /api/workflow` (`WorkflowConfig`).

---

## Budget Engine

| Component | Responsibility |
|-----------|----------------|
| `EscalationPolicyEngine` | Remaining budget math; amount threshold; escalate-on-exceed |
| `BudgetPolicyEngine` | Facade for callers / legacy path hints |
| `BudgetService` | Dashboard aggregates; optimistic concurrency on spend |
| Spend timing | Applied when finance commits (enters vendor confirmation) |

Escalation inputs: claim amount, department remaining budget, `WorkflowConfig` thresholds.

---

## Vendor Integration

Port/adapter design ([INTEGRATION.md](INTEGRATION.md)):

```text
FinanceService
    → VendorIntegrationService (port)
        → ResilientVendorIntegrationService (timeout + retry)
            → MockSapVendorService | SapVendorService | OracleErpVendorService | …
```

Configuration: `ers.vendor.provider` / `ERS_VENDOR_PROVIDER`. Business services never import a concrete ERP client.

---

## Deployment topology

| Layer | Technology |
|-------|------------|
| API hosting | Google Cloud Run (container from `ERSBackend/Dockerfile`) |
| Database | Neon PostgreSQL (SSL JDBC) |
| CI/CD | GitHub Actions → Artifact Registry → Cloud Run |
| Local parity | Docker Compose (`backend`, `frontend`, optional `db`) |
| Docs / ops | springdoc OpenAPI, Actuator health & metrics |

Full runbooks: [DEPLOYMENT.md](DEPLOYMENT.md).

---

## Repository layout

```text
.
├── ERSBackend/                 Spring Boot API
│   ├── Dockerfile
│   └── src/main/java/com/reimbursement/
│       ├── controller/
│       ├── security/
│       ├── workflow/
│       ├── policy/             Budget / escalation engines
│       ├── integration/vendor/ ERP adapters
│       └── config/             OpenAPI, CORS
├── ers-frontend/               React + TypeScript SPA
│   ├── Dockerfile
│   └── nginx.conf
├── .github/workflows/          CI + Cloud Run deploy
├── docker-compose.yml
├── DEPLOYMENT.md
├── ARCHITECTURE.md
├── WORKFLOW.md
└── INTEGRATION.md
```

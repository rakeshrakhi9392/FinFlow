# Production Code Review — Employee Reimbursement System (FinFlow)

**Reviewer stance:** Google Staff Engineer production readiness review  
**Scope:** Full-stack monorepo (`ERSBackend` + `ers-frontend`)  
**Date:** 2026-07-24

This document records architecture, tradeoffs, known limitations, and future improvements after a production-oriented cleanup pass (security hardening, dead-code removal, API/validation consistency, SQL fetch strategies, SPA route guards, accessibility/responsiveness). **No new product features were added.**

---

## Architecture

```text
React SPA (CRA + TypeScript)
    │  HTTPS + session cookie (JSESSIONID) + CORS allow-list
    ▼
Spring Boot 3.2 API (Cloud Run or local)
    ├── SecurityConfig          RBAC matchers + method security
    ├── Controllers             Thin HTTP adapters (/users, /api/*)
    ├── Services                User, Reimbursement, Budget, Finance, Workflow
    ├── Workflow + Policy       State machine, escalation, ownership checks
    ├── Vendor port             ResilientVendor → Mock SAP (stubs for SAP/Oracle/FX)
    └── Spring Data JPA         PostgreSQL / Neon (H2 in tests)
```

### Backend layers

| Layer | Responsibility |
|-------|----------------|
| `controller` | Auth/session extraction, validation annotations, HTTP status mapping |
| `service` | Transactions, orchestration, business rules |
| `workflow` / `policy` | Status transitions, escalation, budget/ownership authorization |
| `integration.vendor` | ERP posting port + resilience decorator |
| `repository` / `entity` | Persistence; claim list queries use `JOIN FETCH` to avoid N+1 |
| `security` | Session ↔ `SecurityContext`, CORS (single source), password hashing |

### Frontend structure

| Area | Responsibility |
|------|----------------|
| `features/auth` | Login / register / logout |
| `features/dashboard` | Role dashboards; shared `ApprovalWorkspace` |
| `features/reimbursement` | Submit form, claim cards, timeline/history |
| `shared/components` | `ProtectedRoute` (role-aware SPA guards) |
| `context` | `sessionStorage` mirror of logged-in user (server session is authoritative) |
| `services` | Axios client (`withCredentials`) |

### Claim lifecycle

`SUBMITTED` → `MANAGER_REVIEW` → optional `SENIOR_MANAGER_REVIEW` → `FINANCE_REVIEW` → `PENDING_VENDOR_CONFIRMATION` → (`FAILED_VENDOR_SYNC` \| `VENDOR_PROCESSING`) → `PAID` \| `DENIED`

Escalation is driven by `workflow_config` (amount threshold + remaining department budget).

---

## Tradeoffs

| Decision | Why | Cost |
|----------|-----|------|
| **Session cookies instead of JWT** | Simpler revocation, fits same-site or CORS+credentials SPA | Sticky sessions / external session store needed on multi-instance Cloud Run |
| **CSRF disabled** | SPA and API are **cross-origin**; cookie CSRF tokens are not readable by the SPA JS origin | Relies on CORS allow-list + `SameSite` cookies; weaker than same-origin + CSRF |
| **Single CORS source (`SecurityConfig`)** | Removed conflicting `CorsConfig` + per-controller `@CrossOrigin` | Controllers no longer declare origins locally (by design) |
| **Self-registration limited to employee/manager** | Blocks privilege escalation via public `POST /users` | Elevated roles must be provisioned out-of-band (seed / admin ops) |
| **Demo seed excluded from `prod`** | Avoid shipping `password` demo accounts to Neon/Cloud Run | Prod must seed reference data and users explicitly |
| **Swagger off by default in `prod`** | Shrinks attack surface | Enable via `SPRINGDOC_*` env when needed for demos |
| **`ddl-auto=update` retained** | Keeps demo/local DX simple | Not a substitute for Flyway/Liquibase in real production |
| **Money as `double`** | Avoided a wide-breaking schema/DTO migration in this pass | Precision risk on budget/spend arithmetic |
| **Shared `ApprovalWorkspace`** | Removes duplicated manager/senior/finance/admin queue UI | Role-specific UX is thinner |

---

## Known limitations

1. **CSRF** — Disabled for cross-origin SPA compatibility. Prefer reverse-proxy same-origin deployment, then enable Spring CSRF + SPA token handling.
2. **In-memory HTTP sessions** — Cloud Run needs session affinity or an external session store (Redis) for multi-instance correctness.
3. **Schema management** — `spring.jpa.hibernate.ddl-auto=update` (local and prod profile) can drift; no versioned migrations yet.
4. **Monetary types** — Claim and budget amounts use IEEE `double`, not `BigDecimal` / `NUMERIC`.
5. **Department scoping** — Managers with `canViewAllReimbursements` can see org-wide claims; no department-scoped queues.
6. **Vendor adapters** — Only Mock SAP is functional; SAP / Oracle / FX classes are intentional stubs.
7. **SPA auth mirror** — Route guards use `sessionStorage`; a forged client role cannot bypass API RBAC, but UX can desync until a 401 redirect.
8. **Legacy statuses** — Enum still contains `MANAGER_APPROVAL`, `REQUIRES_SENIOR_APPROVAL`, `APPROVED` for compatibility.
9. **OpenAPI public in non-prod** — Swagger remains open locally by design.
10. **Password policy** — Minimum length enforced (8); no complexity / breach checks / lockout.

### Cleanup completed in this pass (summary)

- Blocked elevated-role self-registration; stronger registration/claim validation  
- Consolidated CORS; removed dead `@CrossOrigin` / `CorsConfig`  
- Fixed SPA auth (`isAuthenticated = true` → real `ProtectedRoute`)  
- Removed dead repository method, legacy throw-only service overloads, empty integration marker, unused dashboard CSS  
- Claim list queries use entity graph-style `JOIN FETCH`; status/user/date indexes on `reimbursement_requests`  
- Consistent `MessageResponse` on user mutate endpoints; `201` on claim create  
- Exception handler logs unhandled errors; handles malformed JSON  
- Auth/forms: labels, alerts, keyboard-friendly links, mobile breakpoints  
- Swagger default-off in prod; demo seed disabled in prod; workflow config bootstrap always runs  

**Verification:** Backend `mvn test` passed; frontend `npm run build` succeeded.

---

## Future improvements

### Security & ops
- Serve SPA and API same-origin (Cloud Load Balancing / nginx) and **enable CSRF**
- Externalize sessions (Redis) for Cloud Run horizontal scale
- Replace `ddl-auto` with **Flyway** migrations; pin schema in prod to `validate`
- Rotate/remove weak demo credentials; add account lockout and stronger password rules
- Network-restrict Actuator and Swagger behind IAM

### Domain & data
- Migrate money fields to `BigDecimal` / `NUMERIC(19,4)`
- Department-scoped manager queues and admin user provisioning API
- Soft-delete users; audit log retention policy
- Drop legacy status aliases after a data migration

### Product & UX
- Replace `alert`-era patterns fully with toasts; optimistic UI for approvals
- Server-driven “current user” endpoint to refresh SPA session mirror
- Paginate claim queues; add search/filter API params
- Implement real SAP/Oracle adapters behind the existing port

### Engineering
- Expand integration tests (MockMvc) for authz matrix and registration role denial
- Contract tests for OpenAPI vs frontend types
- Consider React Query (or similar) for cache/invalidation on queue refresh
- CI gate on dependency vulnerability scanning

---

## Author / ownership

Original project: **Madasu Rakesh** (FinFlow). This `REVIEW.md` captures the production review and hardening notes for maintainers.

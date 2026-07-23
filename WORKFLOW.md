# Enterprise Approval Workflow

This document describes the multi-stage reimbursement approval workflow, escalation rules, RBAC, and API surface.

## Lifecycle

```text
Submitted
    ↓
Manager Review
    ↓
Senior Manager Review   ← only when escalation rules apply
    ↓
Finance Review
    ↓
Pending Vendor Confirmation  ← Mock SAP / ERP post (timeout + retry)
    ↓                        ↘ Failed Vendor Sync (retryable)
Vendor Processing            ← ERP confirmed (accounting document present)
    ↓
Paid
```

Any **review** stage (Manager, Senior Manager, Finance) may transition to **Denied**.

### Status values

| Status | Meaning |
|--------|---------|
| `SUBMITTED` | Transient create state (immediately advances) |
| `MANAGER_REVIEW` | Waiting on manager |
| `SENIOR_MANAGER_REVIEW` | Waiting on senior manager (escalated) |
| `FINANCE_REVIEW` | Waiting on finance |
| `PENDING_VENDOR_CONFIRMATION` | Finance committed; ERP sync in progress / pending |
| `FAILED_VENDOR_SYNC` | ERP sync failed; finance may retry |
| `VENDOR_PROCESSING` | ERP confirmed; awaiting payment confirmation |
| `PAID` | Terminal success |
| `DENIED` | Terminal rejection |

Budget spend is applied when a claim first enters vendor confirmation (finance commitment).

Vendor / ERP architecture, resilience settings, and dashboard APIs are documented in [INTEGRATION.md](./INTEGRATION.md).

## Escalation rules (configurable)

Stored in `workflow_config` (key `DEFAULT`). Admins update via `PUT /api/workflow`.

| Rule | Default | Behavior |
|------|---------|----------|
| Senior amount threshold | `$5,000` | Claim amount ≥ threshold → `requiresSeniorReview` |
| Escalate on budget exceed | `true` | Amount > remaining department budget → escalate |
| Senior stage enabled | `true` | When false, skip senior stage entirely |
| Finance stage enabled | `true` | When false, skip finance → vendor after manager/senior |

Escalation is evaluated **at submit time** and stored on the claim (`requiresSeniorReview`, `escalatedByAmount`, `escalatedByBudget`).

Manager approval then routes:

- If escalated **and** senior stage enabled → `SENIOR_MANAGER_REVIEW`
- Else if finance enabled → `FINANCE_REVIEW`
- Else → `VENDOR_PROCESSING`

## Approval history & timestamps

Every transition writes an `approval_history` row:

- Actor (user)
- Actor role
- Action (`SUBMITTED`, `APPROVED`, `DENIED`, `ESCALATED`, `VENDOR_MARKED_PAID`)
- From / to status
- Optional comment
- `actedAt` timestamp

API responses include:

- `approvalHistory` — chronological audit trail
- `timeline` — UI-ready stage list (`completed` / `current` / `upcoming` / `skipped` / `denied`)
- `statusChangedAt` — last status change instant
- `allowedActions` — role-specific buttons (`APPROVE`, `DENY`, `MARK_PAID`)

## Roles (RBAC)

| Role | Value | Capabilities |
|------|-------|--------------|
| Employee | `employee` | Submit claims; view own timeline/history |
| Manager | `manager` | Act on `MANAGER_REVIEW` |
| Senior Manager | `senior_manager` | Act on `SENIOR_MANAGER_REVIEW` |
| Finance | `finance` | Act on `FINANCE_REVIEW`; mark `VENDOR_PROCESSING` → `PAID` |
| Admin | `admin` | All stages; configure workflow; delete users |

Spring Security authorities: `ROLE_EMPLOYEE`, `ROLE_MANAGER`, `ROLE_SENIOR_MANAGER`, `ROLE_FINANCE`, `ROLE_ADMIN`.

Method security (`@PreAuthorize`) and HTTP matchers enforce endpoint access. Workflow code also validates that the actor’s role may act on the claim’s current status.

## Demo users

Password for all: `password`

| Username | Role |
|----------|------|
| `employee1` | employee |
| `manager1` | manager |
| `senior1` | senior_manager |
| `finance1` | finance |
| `admin1` | admin |

## Key API endpoints

| Method | Path | Who |
|--------|------|-----|
| `POST` | `/users/login` | Public |
| `POST` | `/users/logout` | Authenticated |
| `POST` | `/api/reimbursements` | Employee, Admin |
| `GET` | `/api/reimbursements/queue` | Manager+ |
| `GET` | `/api/reimbursements/{id}` | Owner or elevated |
| `POST` | `/api/reimbursements/approve/{id}` | Manager / Senior / Finance / Admin |
| `POST` | `/api/reimbursements/deny/{id}` | Manager / Senior / Finance / Admin |
| `POST` | `/api/reimbursements/mark-paid/{id}` | Finance, Admin |
| `GET` | `/api/vendor` | Finance, Admin — vendor integration dashboard |
| `POST` | `/api/vendor/{id}/retry` | Finance, Admin — retry failed / pending ERP sync |
| `GET` | `/api/workflow` | Elevated roles |
| `PUT` | `/api/workflow` | Admin |

Approval body (optional):

```json
{ "comment": "Looks good — within policy." }
```

## Frontend

Role-specific dashboards:

- `/employee-dashboard` — submit + personal timeline cards
- `/manager-dashboard` — manager queue
- `/senior-dashboard` — senior queue
- `/finance-dashboard` — finance + mark paid + link to vendor dashboard
- `/vendor-dashboard` — integration status, last sync, vendor response, retry
- `/admin-dashboard` — all claims + escalation config

Each claim card supports status badges, expandable approval timeline, history with comments/timestamps, and role-gated actions.

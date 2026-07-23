# Vendor / ERP Integration Architecture

Production-oriented integration layer between finance approval and external ERPs.

## Call chain

```text
Controller (Reimbursement / Vendor)
        ↓
FinanceService
        ↓
VendorIntegrationService   ← interface (port)
        ↓
ResilientVendorIntegrationService  ← timeout + retry decorator
        ↓
MockSapVendorService       ← current adapter (or SAP / Oracle / FX)
```

Business code never imports a concrete ERP client. Swapping vendors is a configuration change.

## Ports and adapters

| Component | Role |
|-----------|------|
| `VendorIntegrationService` | Port: `vendorSystem()`, `postReimbursement(request)` |
| `VendorPostingRequest` | Vendor-agnostic input DTO (no JPA entities) |
| `VendorPostingResult` | Normalized ERP output |
| `MockSapVendorService` | Simulated SAP FI posting (`ers.vendor.provider=mock-sap`) |
| `SapVendorService` | Placeholder real SAP adapter (`provider=sap`) |
| `OracleErpVendorService` | Placeholder Oracle adapter (`provider=oracle`) |
| `ExchangeRateVendorService` | Placeholder FX enrichment (`provider=exchange-rate`) |
| `ResilientVendorIntegrationService` | `@Primary` bean: per-attempt timeout + retries |

### Adding a future vendor without changing business code

1. Implement `VendorIntegrationService` under `integration/vendor/<name>/`.
2. Annotate `@Service("vendorAdapter")` + `@ConditionalOnProperty(name="ers.vendor.provider", havingValue="...")`.
3. Set `ers.vendor.provider=...` in `application.properties`.
4. `FinanceService` and controllers remain unchanged.

## Lifecycle after finance approval

```text
FINANCE_REVIEW
      ↓ approve (budget spend applied once)
PENDING_VENDOR_CONFIRMATION
      ↓ Mock SAP / ERP call (with timeout + retry)
      ├─ success → VENDOR_PROCESSING  (+ accounting document, reference, posting date, vendor id, payment status)
      └─ failure → FAILED_VENDOR_SYNC  (retryable)
VENDOR_PROCESSING
      ↓ mark paid (requires successful sync + accounting document)
PAID
```

### Error / sync states

| Status | Meaning |
|--------|---------|
| `PENDING_VENDOR_CONFIRMATION` | ERP call in flight or awaiting confirmation |
| `FAILED_VENDOR_SYNC` | Retries exhausted or hard failure; **Retry** enabled |
| `VendorSyncStatus.SYNCED` | ERP accepted posting |

## ERP response fields

Persisted on the claim and shown on the Vendor Dashboard:

- Accounting Document
- Reference Number
- Posting Date
- Vendor Id
- Payment Status
- Last sync timestamp
- Raw vendor response
- Error code / message (on failure)
- Sync attempt count

## Resilience

Configured via `ers.vendor.*`:

| Property | Default | Purpose |
|----------|---------|---------|
| `timeout-ms` | `3000` | Per-attempt call timeout |
| `max-attempts` | `3` | Including the first call |
| `retry-backoff-ms` | `250` | Linear backoff base × attempt |
| `mock-fail-every-nth-id` | `7` | Deterministic mock FI failures |
| `mock-timeout-every-nth-id` | `13` | Deterministic mock timeouts |

## API

| Method | Path | Role |
|--------|------|------|
| `GET` | `/api/vendor` | Finance, Admin — dashboard list |
| `GET` | `/api/vendor/{id}` | Finance, Admin — detail |
| `POST` | `/api/vendor/{id}/retry` | Finance, Admin — retry sync |
| `POST` | `/api/reimbursements/approve/{id}` | Triggers vendor post when landing on pending confirmation |
| `POST` | `/api/reimbursements/mark-paid/{id}` | Requires successful vendor sync |

## UI

- **Finance Dashboard** — approve/deny/mark paid; link to Vendor Dashboard; queue retry action.
- **Vendor Dashboard** (`/vendor-dashboard`) — integration status, last sync, vendor response, **Retry** button.

Login as `finance1` / `password` to exercise the flow.

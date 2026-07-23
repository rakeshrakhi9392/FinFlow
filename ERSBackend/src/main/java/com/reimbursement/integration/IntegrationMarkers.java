package com.reimbursement.integration;

/**
 * Integration adapters for external systems (email, payroll, document storage, etc.).
 * Intentionally empty in this refactor — reserved for future outbound integrations
 * so feature code does not grow ad-hoc HTTP/client calls in the service layer.
 */
public final class IntegrationMarkers {

    private IntegrationMarkers() {
    }
}

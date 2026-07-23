package com.reimbursement.integration;

/**
 * Outbound integration adapters.
 *
 * <p>Vendor / ERP posting lives under {@code com.reimbursement.integration.vendor}.
 * Business services depend on {@link com.reimbursement.integration.vendor.VendorIntegrationService}
 * only — never on a concrete ERP client.</p>
 */
public final class IntegrationMarkers {

    private IntegrationMarkers() {
    }
}

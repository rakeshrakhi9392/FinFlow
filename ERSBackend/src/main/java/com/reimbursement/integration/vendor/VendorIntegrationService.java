package com.reimbursement.integration.vendor;

/**
 * Port for external vendor / ERP posting.
 *
 * <p>Business code (e.g. {@code FinanceService}) depends only on this interface.
 * Swap implementations without changing approval logic:</p>
 * <ul>
 *   <li>{@code MockSapVendorService} — local ERP simulation</li>
 *   <li>{@code SapVendorService} — real SAP BAPI / OData</li>
 *   <li>{@code OracleErpVendorService} — Oracle Fusion / EBS</li>
 *   <li>{@code ExchangeRateVendorService} — FX enrichment before posting</li>
 * </ul>
 */
public interface VendorIntegrationService {

    /** Stable identifier for the active vendor system (e.g. {@code MOCK_SAP}). */
    String vendorSystem();

    /**
     * Posts an approved reimbursement to the vendor ERP.
     *
     * @throws VendorIntegrationException on hard failures after any internal adapter logic
     */
    VendorPostingResult postReimbursement(VendorPostingRequest request);
}

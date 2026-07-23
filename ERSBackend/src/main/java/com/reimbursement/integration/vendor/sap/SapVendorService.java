package com.reimbursement.integration.vendor.sap;

import com.reimbursement.integration.vendor.VendorIntegrationException;
import com.reimbursement.integration.vendor.VendorIntegrationService;
import com.reimbursement.integration.vendor.VendorPostingRequest;
import com.reimbursement.integration.vendor.VendorPostingResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Placeholder for a real SAP FI/CO adapter (BAPI / OData / RFC).
 * Enable with {@code ers.vendor.provider=sap} once credentials and mapping are configured.
 */
@Service("vendorAdapter")
@ConditionalOnProperty(name = "ers.vendor.provider", havingValue = "sap")
public class SapVendorService implements VendorIntegrationService {

    @Override
    public String vendorSystem() {
        return "SAP";
    }

    @Override
    public VendorPostingResult postReimbursement(VendorPostingRequest request) {
        throw new VendorIntegrationException(
                "SAP adapter is not configured in this environment",
                "SAP_NOT_CONFIGURED",
                false);
    }
}

package com.reimbursement.integration.vendor.oracle;

import com.reimbursement.integration.vendor.VendorIntegrationException;
import com.reimbursement.integration.vendor.VendorIntegrationService;
import com.reimbursement.integration.vendor.VendorPostingRequest;
import com.reimbursement.integration.vendor.VendorPostingResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Placeholder for Oracle Fusion / EBS AP invoice posting.
 * Enable with {@code ers.vendor.provider=oracle}.
 */
@Service("vendorAdapter")
@ConditionalOnProperty(name = "ers.vendor.provider", havingValue = "oracle")
public class OracleErpVendorService implements VendorIntegrationService {

    @Override
    public String vendorSystem() {
        return "ORACLE_ERP";
    }

    @Override
    public VendorPostingResult postReimbursement(VendorPostingRequest request) {
        throw new VendorIntegrationException(
                "Oracle ERP adapter is not configured in this environment",
                "ORACLE_NOT_CONFIGURED",
                false);
    }
}

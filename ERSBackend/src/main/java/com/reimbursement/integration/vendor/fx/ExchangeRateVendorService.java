package com.reimbursement.integration.vendor.fx;

import com.reimbursement.integration.vendor.VendorIntegrationException;
import com.reimbursement.integration.vendor.VendorIntegrationService;
import com.reimbursement.integration.vendor.VendorPostingRequest;
import com.reimbursement.integration.vendor.VendorPostingResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Placeholder for an FX / exchange-rate enrichment vendor used before ERP posting.
 * Enable with {@code ers.vendor.provider=exchange-rate}.
 *
 * <p>Typical production shape: decorate another {@link VendorIntegrationService} to
 * convert currency, then post — still without changing FinanceService.</p>
 */
@Service("vendorAdapter")
@ConditionalOnProperty(name = "ers.vendor.provider", havingValue = "exchange-rate")
public class ExchangeRateVendorService implements VendorIntegrationService {

    @Override
    public String vendorSystem() {
        return "EXCHANGE_RATE";
    }

    @Override
    public VendorPostingResult postReimbursement(VendorPostingRequest request) {
        throw new VendorIntegrationException(
                "Exchange-rate vendor adapter is not configured in this environment",
                "FX_NOT_CONFIGURED",
                false);
    }
}

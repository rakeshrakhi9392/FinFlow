package com.reimbursement.integration.vendor.config;

import com.reimbursement.integration.vendor.VendorIntegrationService;
import com.reimbursement.integration.vendor.resilience.ResilientVendorIntegrationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Wires the active ERP adapter behind a resilient primary {@link VendorIntegrationService}.
 *
 * <p>To switch providers later, enable the matching adapter bean
 * ({@code ers.vendor.provider=sap|oracle|exchange-rate}) and point the
 * {@code vendorAdapter} qualifier at that implementation — FinanceService stays unchanged.</p>
 */
@Configuration
@EnableConfigurationProperties(VendorIntegrationProperties.class)
public class VendorIntegrationConfig {

    @Bean
    @Primary
    public VendorIntegrationService vendorIntegrationService(
            @Qualifier("vendorAdapter") VendorIntegrationService vendorAdapter,
            VendorIntegrationProperties properties) {
        return new ResilientVendorIntegrationService(vendorAdapter, properties);
    }
}

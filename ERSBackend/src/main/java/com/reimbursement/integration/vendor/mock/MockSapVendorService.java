package com.reimbursement.integration.vendor.mock;

import com.reimbursement.enums.VendorPaymentStatus;
import com.reimbursement.integration.vendor.VendorIntegrationException;
import com.reimbursement.integration.vendor.VendorIntegrationService;
import com.reimbursement.integration.vendor.VendorPostingRequest;
import com.reimbursement.integration.vendor.VendorPostingResult;
import com.reimbursement.integration.vendor.config.VendorIntegrationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulated SAP FI vendor posting adapter.
 *
 * <p>Produces accounting document / reference / posting date / vendor id / payment status
 * without calling a real ERP. Deterministic failure and timeout hooks support testing
 * retry and error-state UX.</p>
 */
@Service("vendorAdapter")
@ConditionalOnProperty(name = "ers.vendor.provider", havingValue = "mock-sap", matchIfMissing = true)
public class MockSapVendorService implements VendorIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(MockSapVendorService.class);
    private static final DateTimeFormatter DOC_DAY = DateTimeFormatter.BASIC_ISO_DATE;

    private final VendorIntegrationProperties properties;

    public MockSapVendorService(VendorIntegrationProperties properties) {
        this.properties = properties;
    }

    @Override
    public String vendorSystem() {
        return "MOCK_SAP";
    }

    @Override
    public VendorPostingResult postReimbursement(VendorPostingRequest request) {
        log.info("Mock SAP posting reimbursementId={} amount={}", request.reimbursementId(), request.amount());

        sleep(properties.getMockLatencyMs());

        int id = request.reimbursementId();
        int timeoutEvery = properties.getMockTimeoutEveryNthId();
        if (timeoutEvery > 0 && id % timeoutEvery == 0) {
            sleep(properties.getTimeoutMs() + 500);
            throw new VendorIntegrationException(
                    "Mock SAP timed out waiting for FI document creation",
                    "SAP_TIMEOUT",
                    true);
        }

        int failEvery = properties.getMockFailEveryNthId();
        if (failEvery > 0 && id % failEvery == 0) {
            throw new VendorIntegrationException(
                    "Mock SAP rejected posting: company code lock / temporary FI error",
                    "SAP_FI_LOCK",
                    true);
        }

        if (request.amount() <= 0) {
            return VendorPostingResult.failure(
                    "SAP_INVALID_AMOUNT",
                    "Amount must be positive",
                    "{\"status\":\"E\",\"message\":\"Invalid amount\"}");
        }

        LocalDate postingDate = LocalDate.now();
        String companyCode = "1000";
        String fiscalYear = String.valueOf(postingDate.getYear());
        String docNumber = String.format(Locale.ROOT, "%010d",
                500000000L + id + ThreadLocalRandom.current().nextInt(1000));
        String accountingDocument = companyCode + "-" + fiscalYear + "-" + docNumber;
        String referenceNumber = "ERS-" + id + "-" + postingDate.format(DOC_DAY);
        String vendorId = resolveVendorId(request);
        VendorPaymentStatus paymentStatus = VendorPaymentStatus.SCHEDULED_FOR_PAYMENT;

        String raw = String.format(Locale.ROOT,
                "{\"system\":\"MOCK_SAP\",\"bukrs\":\"%s\",\"belnr\":\"%s\",\"gjahr\":\"%s\","
                        + "\"xref1\":\"%s\",\"lifnr\":\"%s\",\"paymentStatus\":\"%s\"}",
                companyCode, docNumber, fiscalYear, referenceNumber, vendorId, paymentStatus);

        return VendorPostingResult.success(
                accountingDocument,
                referenceNumber,
                postingDate,
                vendorId,
                paymentStatus,
                raw);
    }

    private String resolveVendorId(VendorPostingRequest request) {
        if (request.employeeVendorKey() != null && !request.employeeVendorKey().isBlank()) {
            return "V-" + request.employeeVendorKey().toUpperCase(Locale.ROOT);
        }
        return "V-EMP-" + request.reimbursementId();
    }

    private void sleep(long ms) {
        if (ms <= 0) {
            return;
        }
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new VendorIntegrationException("Mock SAP interrupted", "SAP_INTERRUPTED", true, e);
        }
    }
}

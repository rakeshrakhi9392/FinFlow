package com.reimbursement.integration.vendor;

import com.reimbursement.enums.VendorPaymentStatus;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Normalized ERP response returned by any {@link VendorIntegrationService} implementation.
 */
public record VendorPostingResult(
        boolean success,
        String accountingDocument,
        String referenceNumber,
        LocalDate postingDate,
        String vendorId,
        VendorPaymentStatus paymentStatus,
        String rawResponse,
        String errorCode,
        String errorMessage,
        Instant respondedAt
) {
    public static VendorPostingResult success(String accountingDocument,
                                              String referenceNumber,
                                              LocalDate postingDate,
                                              String vendorId,
                                              VendorPaymentStatus paymentStatus,
                                              String rawResponse) {
        return new VendorPostingResult(
                true,
                accountingDocument,
                referenceNumber,
                postingDate,
                vendorId,
                paymentStatus,
                rawResponse,
                null,
                null,
                Instant.now());
    }

    public static VendorPostingResult failure(String errorCode, String errorMessage, String rawResponse) {
        return new VendorPostingResult(
                false,
                null,
                null,
                null,
                null,
                VendorPaymentStatus.NOT_POSTED,
                rawResponse,
                errorCode,
                errorMessage,
                Instant.now());
    }
}

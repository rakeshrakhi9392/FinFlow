package com.reimbursement.integration.vendor;

/**
 * Checked-style unchecked exception for vendor / ERP integration failures.
 */
public class VendorIntegrationException extends RuntimeException {

    private final String errorCode;
    private final boolean retryable;

    public VendorIntegrationException(String message, String errorCode, boolean retryable) {
        super(message);
        this.errorCode = errorCode;
        this.retryable = retryable;
    }

    public VendorIntegrationException(String message, String errorCode, boolean retryable, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.retryable = retryable;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public boolean isRetryable() {
        return retryable;
    }
}

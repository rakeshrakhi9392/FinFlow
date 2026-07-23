package com.reimbursement.enums;

/**
 * Payment lifecycle as reported by the external vendor / ERP system.
 */
public enum VendorPaymentStatus {
    NOT_POSTED,
    QUEUED,
    POSTED,
    SCHEDULED_FOR_PAYMENT,
    PAID,
    REJECTED,
    UNKNOWN
}

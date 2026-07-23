package com.reimbursement.enums;

/**
 * Integration sync state between ERS and the external vendor ERP.
 *
 * <p>Claim workflow statuses {@link ReimbursementStatus#PENDING_VENDOR_CONFIRMATION} and
 * {@link ReimbursementStatus#FAILED_VENDOR_SYNC} mirror these values for queue routing.</p>
 */
public enum VendorSyncStatus {
    NOT_STARTED,
    PENDING_VENDOR_CONFIRMATION,
    SYNCED,
    FAILED_VENDOR_SYNC
}

package com.reimbursement.enums;

/**
 * Enterprise reimbursement lifecycle.
 *
 * <pre>
 * SUBMITTED → MANAGER_REVIEW → [SENIOR_MANAGER_REVIEW] → FINANCE_REVIEW
 *   → PENDING_VENDOR_CONFIRMATION → VENDOR_PROCESSING → PAID
 *                              ↘ FAILED_VENDOR_SYNC (retryable)
 * </pre>
 *
 * Any review stage may transition to {@link #DENIED}.
 */
public enum ReimbursementStatus {
    SUBMITTED,
    MANAGER_REVIEW,
    SENIOR_MANAGER_REVIEW,
    FINANCE_REVIEW,
    /** Finance committed; awaiting / in-flight ERP confirmation. */
    PENDING_VENDOR_CONFIRMATION,
    /** ERP sync failed after retries; eligible for manual retry. */
    FAILED_VENDOR_SYNC,
    /** ERP confirmed; awaiting finance mark-paid. */
    VENDOR_PROCESSING,
    PAID,
    DENIED,

    /** @deprecated Legacy — prefer {@link #MANAGER_REVIEW} */
    @Deprecated
    MANAGER_APPROVAL,
    /** @deprecated Legacy — prefer {@link #SENIOR_MANAGER_REVIEW} */
    @Deprecated
    REQUIRES_SENIOR_APPROVAL,
    /** @deprecated Legacy — prefer {@link #PAID} */
    @Deprecated
    APPROVED;

    public static ReimbursementStatus fromLegacy(String value) {
        if (value == null) {
            return MANAGER_REVIEW;
        }
        if ("PENDING".equalsIgnoreCase(value) || "Pending".equals(value)) {
            return MANAGER_REVIEW;
        }
        if ("MANAGER_APPROVAL".equalsIgnoreCase(value)) {
            return MANAGER_REVIEW;
        }
        if ("REQUIRES_SENIOR_APPROVAL".equalsIgnoreCase(value)) {
            return SENIOR_MANAGER_REVIEW;
        }
        if ("APPROVED".equalsIgnoreCase(value) || "Approved".equals(value)) {
            return PAID;
        }
        if ("Denied".equals(value)) {
            return DENIED;
        }
        return ReimbursementStatus.valueOf(value.toUpperCase().replace(' ', '_'));
    }

    public boolean isTerminal() {
        return this == PAID || this == DENIED;
    }

    public boolean isReviewStage() {
        return this == MANAGER_REVIEW
                || this == SENIOR_MANAGER_REVIEW
                || this == FINANCE_REVIEW;
    }

    public boolean isVendorIntegrationStage() {
        return this == PENDING_VENDOR_CONFIRMATION
                || this == FAILED_VENDOR_SYNC
                || this == VENDOR_PROCESSING;
    }

    public boolean isAwaitingAction() {
        return this == SUBMITTED
                || isReviewStage()
                || isVendorIntegrationStage()
                || this == MANAGER_APPROVAL
                || this == REQUIRES_SENIOR_APPROVAL;
    }

    /** @deprecated Use {@link #isAwaitingAction()} */
    @Deprecated
    public boolean isAwaitingApproval() {
        return isReviewStage()
                || this == MANAGER_APPROVAL
                || this == REQUIRES_SENIOR_APPROVAL;
    }

    public String displayLabel() {
        return switch (this) {
            case SUBMITTED -> "Submitted";
            case MANAGER_REVIEW, MANAGER_APPROVAL -> "Manager Review";
            case SENIOR_MANAGER_REVIEW, REQUIRES_SENIOR_APPROVAL -> "Senior Manager Review";
            case FINANCE_REVIEW -> "Finance Review";
            case PENDING_VENDOR_CONFIRMATION -> "Pending Vendor Confirmation";
            case FAILED_VENDOR_SYNC -> "Failed Vendor Sync";
            case VENDOR_PROCESSING -> "Vendor Processing";
            case PAID, APPROVED -> "Paid";
            case DENIED -> "Denied";
        };
    }
}

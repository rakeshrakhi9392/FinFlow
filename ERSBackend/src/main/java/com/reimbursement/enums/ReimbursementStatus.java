package com.reimbursement.enums;

public enum ReimbursementStatus {
    MANAGER_APPROVAL,
    REQUIRES_SENIOR_APPROVAL,
    APPROVED,
    DENIED;

    /**
     * Legacy PENDING maps to manager-level approval for older clients.
     */
    public static ReimbursementStatus fromLegacy(String value) {
        if (value == null) {
            return MANAGER_APPROVAL;
        }
        if ("PENDING".equalsIgnoreCase(value) || "Pending".equals(value)) {
            return MANAGER_APPROVAL;
        }
        return ReimbursementStatus.valueOf(value.toUpperCase().replace(' ', '_'));
    }

    public boolean isAwaitingApproval() {
        return this == MANAGER_APPROVAL || this == REQUIRES_SENIOR_APPROVAL;
    }
}

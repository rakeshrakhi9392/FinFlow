package com.reimbursement.enums;

/**
 * Discrete actions recorded in the approval history / timeline.
 */
public enum ApprovalAction {
    SUBMITTED,
    APPROVED,
    DENIED,
    ESCALATED,
    VENDOR_SYNC_PENDING,
    VENDOR_SYNC_SUCCESS,
    VENDOR_SYNC_FAILED,
    VENDOR_SYNC_RETRY,
    VENDOR_MARKED_PAID,
    COMMENT,
    SYSTEM
}

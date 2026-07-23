package com.reimbursement.enums;

import java.util.Locale;
import java.util.Set;

/**
 * RBAC roles for the enterprise reimbursement workflow.
 */
public enum UserRole {
    EMPLOYEE("employee"),
    MANAGER("manager"),
    SENIOR_MANAGER("senior_manager"),
    FINANCE("finance"),
    ADMIN("admin");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /** Spring Security authority without ROLE_ prefix handling — use with hasRole. */
    public String springRole() {
        return name();
    }

    public static UserRole fromValue(String value) {
        if (value == null || value.isBlank()) {
            return EMPLOYEE;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        for (UserRole role : values()) {
            if (role.value.equals(normalized) || role.name().equalsIgnoreCase(normalized)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }

    public boolean isEmployee() {
        return this == EMPLOYEE;
    }

    public boolean isManager() {
        return this == MANAGER;
    }

    public boolean isSeniorManager() {
        return this == SENIOR_MANAGER;
    }

    public boolean isFinance() {
        return this == FINANCE;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean canApproveManagerStage() {
        return this == MANAGER || this == ADMIN;
    }

    public boolean canApproveSeniorStage() {
        return this == SENIOR_MANAGER || this == ADMIN;
    }

    public boolean canApproveFinanceStage() {
        return this == FINANCE || this == ADMIN;
    }

    public boolean canProcessVendor() {
        return this == FINANCE || this == ADMIN;
    }

    public boolean canViewAllReimbursements() {
        return this == MANAGER || this == SENIOR_MANAGER || this == FINANCE || this == ADMIN;
    }

    public boolean canConfigureWorkflow() {
        return this == ADMIN;
    }

    public Set<ReimbursementStatus> actionableStatuses() {
        return switch (this) {
            case MANAGER -> Set.of(ReimbursementStatus.MANAGER_REVIEW, ReimbursementStatus.MANAGER_APPROVAL);
            case SENIOR_MANAGER -> Set.of(
                    ReimbursementStatus.SENIOR_MANAGER_REVIEW,
                    ReimbursementStatus.REQUIRES_SENIOR_APPROVAL);
            case FINANCE -> Set.of(
                    ReimbursementStatus.FINANCE_REVIEW,
                    ReimbursementStatus.VENDOR_PROCESSING);
            case ADMIN -> Set.of(
                    ReimbursementStatus.MANAGER_REVIEW,
                    ReimbursementStatus.SENIOR_MANAGER_REVIEW,
                    ReimbursementStatus.FINANCE_REVIEW,
                    ReimbursementStatus.VENDOR_PROCESSING,
                    ReimbursementStatus.MANAGER_APPROVAL,
                    ReimbursementStatus.REQUIRES_SENIOR_APPROVAL);
            default -> Set.of();
        };
    }
}

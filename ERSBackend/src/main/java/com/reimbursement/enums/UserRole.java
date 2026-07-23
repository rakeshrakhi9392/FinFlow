package com.reimbursement.enums;

import java.util.Locale;

public enum UserRole {
    EMPLOYEE("employee"),
    MANAGER("manager");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromValue(String value) {
        if (value == null || value.isBlank()) {
            return EMPLOYEE;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (UserRole role : values()) {
            if (role.value.equals(normalized)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }

    public boolean isManager() {
        return this == MANAGER;
    }
}

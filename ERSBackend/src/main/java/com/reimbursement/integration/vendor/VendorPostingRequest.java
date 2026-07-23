package com.reimbursement.integration.vendor;

import java.time.LocalDate;

/**
 * Vendor-agnostic posting payload. Business services map domain entities into this DTO
 * so ERP adapters never depend on JPA entities.
 */
public record VendorPostingRequest(
        int reimbursementId,
        double amount,
        String currency,
        String description,
        String employeeVendorKey,
        String departmentCode,
        String costCenter,
        String expenseCategoryCode,
        LocalDate expenseDate
) {
}

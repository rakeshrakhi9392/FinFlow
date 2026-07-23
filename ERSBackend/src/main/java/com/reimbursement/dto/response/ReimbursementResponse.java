package com.reimbursement.dto.response;

import com.reimbursement.enums.ReimbursementStatus;

import java.time.LocalDate;

/**
 * API response for reimbursements. Field names match the previous entity JSON
 * contract so existing frontend clients continue to work unchanged.
 */
public class ReimbursementResponse {

    private int reimbursementId;
    private double amount;
    private String description;
    private ReimbursementStatus status;
    private LocalDate dateSubmitted;
    private Long departmentId;
    private String departmentName;
    private Long categoryId;
    private String categoryName;
    private Long budgetId;
    private Double remainingBudgetAtSubmit;

    public ReimbursementResponse() {
    }

    public ReimbursementResponse(int reimbursementId, double amount, String description,
                                 ReimbursementStatus status, LocalDate dateSubmitted) {
        this.reimbursementId = reimbursementId;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.dateSubmitted = dateSubmitted;
    }

    public int getReimbursementId() {
        return reimbursementId;
    }

    public void setReimbursementId(int reimbursementId) {
        this.reimbursementId = reimbursementId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReimbursementStatus getStatus() {
        return status;
    }

    public void setStatus(ReimbursementStatus status) {
        this.status = status;
    }

    public LocalDate getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(LocalDate dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public Double getRemainingBudgetAtSubmit() {
        return remainingBudgetAtSubmit;
    }

    public void setRemainingBudgetAtSubmit(Double remainingBudgetAtSubmit) {
        this.remainingBudgetAtSubmit = remainingBudgetAtSubmit;
    }
}

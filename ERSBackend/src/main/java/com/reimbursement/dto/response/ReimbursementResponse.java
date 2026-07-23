package com.reimbursement.dto.response;

import com.reimbursement.enums.ReimbursementStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * API response for reimbursements including approval history and timeline.
 */
public class ReimbursementResponse {

    private int reimbursementId;
    private double amount;
    private String description;
    private ReimbursementStatus status;
    private String statusLabel;
    private LocalDate dateSubmitted;
    private Instant statusChangedAt;
    private Long departmentId;
    private String departmentName;
    private Long categoryId;
    private String categoryName;
    private Long budgetId;
    private Double remainingBudgetAtSubmit;
    private boolean requiresSeniorReview;
    private boolean escalatedByAmount;
    private boolean escalatedByBudget;
    private Integer submitterId;
    private String submitterUsername;
    private List<String> allowedActions = new ArrayList<>();
    private List<ApprovalHistoryResponse> approvalHistory = new ArrayList<>();
    private WorkflowTimelineResponse timeline;

    public ReimbursementResponse() {
    }

    public ReimbursementResponse(int reimbursementId, double amount, String description,
                                 ReimbursementStatus status, LocalDate dateSubmitted) {
        this.reimbursementId = reimbursementId;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.dateSubmitted = dateSubmitted;
        if (status != null) {
            this.statusLabel = status.displayLabel();
        }
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
        if (status != null) {
            this.statusLabel = status.displayLabel();
        }
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public LocalDate getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(LocalDate dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public Instant getStatusChangedAt() {
        return statusChangedAt;
    }

    public void setStatusChangedAt(Instant statusChangedAt) {
        this.statusChangedAt = statusChangedAt;
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

    public boolean isRequiresSeniorReview() {
        return requiresSeniorReview;
    }

    public void setRequiresSeniorReview(boolean requiresSeniorReview) {
        this.requiresSeniorReview = requiresSeniorReview;
    }

    public boolean isEscalatedByAmount() {
        return escalatedByAmount;
    }

    public void setEscalatedByAmount(boolean escalatedByAmount) {
        this.escalatedByAmount = escalatedByAmount;
    }

    public boolean isEscalatedByBudget() {
        return escalatedByBudget;
    }

    public void setEscalatedByBudget(boolean escalatedByBudget) {
        this.escalatedByBudget = escalatedByBudget;
    }

    public Integer getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(Integer submitterId) {
        this.submitterId = submitterId;
    }

    public String getSubmitterUsername() {
        return submitterUsername;
    }

    public void setSubmitterUsername(String submitterUsername) {
        this.submitterUsername = submitterUsername;
    }

    public List<String> getAllowedActions() {
        return allowedActions;
    }

    public void setAllowedActions(List<String> allowedActions) {
        this.allowedActions = allowedActions;
    }

    public List<ApprovalHistoryResponse> getApprovalHistory() {
        return approvalHistory;
    }

    public void setApprovalHistory(List<ApprovalHistoryResponse> approvalHistory) {
        this.approvalHistory = approvalHistory;
    }

    public WorkflowTimelineResponse getTimeline() {
        return timeline;
    }

    public void setTimeline(WorkflowTimelineResponse timeline) {
        this.timeline = timeline;
    }
}

package com.reimbursement.dto.response;

import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.enums.VendorPaymentStatus;
import com.reimbursement.enums.VendorSyncStatus;

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
    private VendorSyncStatus vendorSyncStatus;
    private String vendorSystem;
    private String accountingDocument;
    private String vendorReferenceNumber;
    private LocalDate vendorPostingDate;
    private String vendorId;
    private VendorPaymentStatus vendorPaymentStatus;
    private Instant lastVendorSyncAt;
    private int vendorSyncAttempts;
    private String vendorResponse;
    private String vendorErrorCode;
    private String vendorErrorMessage;

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

    public VendorSyncStatus getVendorSyncStatus() {
        return vendorSyncStatus;
    }

    public void setVendorSyncStatus(VendorSyncStatus vendorSyncStatus) {
        this.vendorSyncStatus = vendorSyncStatus;
    }

    public String getVendorSystem() {
        return vendorSystem;
    }

    public void setVendorSystem(String vendorSystem) {
        this.vendorSystem = vendorSystem;
    }

    public String getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(String accountingDocument) {
        this.accountingDocument = accountingDocument;
    }

    public String getVendorReferenceNumber() {
        return vendorReferenceNumber;
    }

    public void setVendorReferenceNumber(String vendorReferenceNumber) {
        this.vendorReferenceNumber = vendorReferenceNumber;
    }

    public LocalDate getVendorPostingDate() {
        return vendorPostingDate;
    }

    public void setVendorPostingDate(LocalDate vendorPostingDate) {
        this.vendorPostingDate = vendorPostingDate;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public VendorPaymentStatus getVendorPaymentStatus() {
        return vendorPaymentStatus;
    }

    public void setVendorPaymentStatus(VendorPaymentStatus vendorPaymentStatus) {
        this.vendorPaymentStatus = vendorPaymentStatus;
    }

    public Instant getLastVendorSyncAt() {
        return lastVendorSyncAt;
    }

    public void setLastVendorSyncAt(Instant lastVendorSyncAt) {
        this.lastVendorSyncAt = lastVendorSyncAt;
    }

    public int getVendorSyncAttempts() {
        return vendorSyncAttempts;
    }

    public void setVendorSyncAttempts(int vendorSyncAttempts) {
        this.vendorSyncAttempts = vendorSyncAttempts;
    }

    public String getVendorResponse() {
        return vendorResponse;
    }

    public void setVendorResponse(String vendorResponse) {
        this.vendorResponse = vendorResponse;
    }

    public String getVendorErrorCode() {
        return vendorErrorCode;
    }

    public void setVendorErrorCode(String vendorErrorCode) {
        this.vendorErrorCode = vendorErrorCode;
    }

    public String getVendorErrorMessage() {
        return vendorErrorMessage;
    }

    public void setVendorErrorMessage(String vendorErrorMessage) {
        this.vendorErrorMessage = vendorErrorMessage;
    }
}

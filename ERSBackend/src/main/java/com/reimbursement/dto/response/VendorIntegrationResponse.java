package com.reimbursement.dto.response;

import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.enums.VendorPaymentStatus;
import com.reimbursement.enums.VendorSyncStatus;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Vendor ERP integration snapshot for dashboards and claim detail.
 */
public class VendorIntegrationResponse {

    private int reimbursementId;
    private double amount;
    private String description;
    private ReimbursementStatus status;
    private String statusLabel;
    private String submitterUsername;
    private String departmentName;

    private VendorSyncStatus integrationStatus;
    private String vendorSystem;
    private String accountingDocument;
    private String referenceNumber;
    private LocalDate postingDate;
    private String vendorId;
    private VendorPaymentStatus paymentStatus;
    private Instant lastSyncAt;
    private int syncAttempts;
    private String vendorResponse;
    private String errorCode;
    private String errorMessage;
    private boolean retryAllowed;

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

    public String getSubmitterUsername() {
        return submitterUsername;
    }

    public void setSubmitterUsername(String submitterUsername) {
        this.submitterUsername = submitterUsername;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public VendorSyncStatus getIntegrationStatus() {
        return integrationStatus;
    }

    public void setIntegrationStatus(VendorSyncStatus integrationStatus) {
        this.integrationStatus = integrationStatus;
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

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public LocalDate getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(LocalDate postingDate) {
        this.postingDate = postingDate;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public VendorPaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(VendorPaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Instant getLastSyncAt() {
        return lastSyncAt;
    }

    public void setLastSyncAt(Instant lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }

    public int getSyncAttempts() {
        return syncAttempts;
    }

    public void setSyncAttempts(int syncAttempts) {
        this.syncAttempts = syncAttempts;
    }

    public String getVendorResponse() {
        return vendorResponse;
    }

    public void setVendorResponse(String vendorResponse) {
        this.vendorResponse = vendorResponse;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isRetryAllowed() {
        return retryAllowed;
    }

    public void setRetryAllowed(boolean retryAllowed) {
        this.retryAllowed = retryAllowed;
    }
}

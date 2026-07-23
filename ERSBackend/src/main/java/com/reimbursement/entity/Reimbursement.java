package com.reimbursement.entity;

import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.enums.VendorPaymentStatus;
import com.reimbursement.enums.VendorSyncStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "reimbursement_requests",
        indexes = {
                @Index(name = "idx_reimb_status", columnList = "status"),
                @Index(name = "idx_reimb_user", columnList = "user_id"),
                @Index(name = "idx_reimb_date_submitted", columnList = "dateSubmitted")
        }
)
public class Reimbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reimbursement_id")
    private int reimbursementId;

    @Column(nullable = false)
    private double amount;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReimbursementStatus status;

    @Column(nullable = false)
    private LocalDate dateSubmitted;

    @Column(nullable = false)
    private boolean requiresSeniorReview;

    @Column(nullable = false)
    private boolean escalatedByAmount;

    @Column(nullable = false)
    private boolean escalatedByBudget;

    @Column
    private Instant statusChangedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VendorSyncStatus vendorSyncStatus = VendorSyncStatus.NOT_STARTED;

    @Column(length = 64)
    private String vendorSystem;

    @Column(length = 64)
    private String accountingDocument;

    @Column(length = 64)
    private String vendorReferenceNumber;

    @Column
    private LocalDate vendorPostingDate;

    @Column(length = 64)
    private String vendorId;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private VendorPaymentStatus vendorPaymentStatus;

    @Column
    private Instant lastVendorSyncAt;

    @Column(nullable = false)
    private int vendorSyncAttempts;

    @Lob
    @Column
    private String vendorResponse;

    @Column(length = 64)
    private String vendorErrorCode;

    @Column(length = 500)
    private String vendorErrorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ExpenseCategory expenseCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @OneToMany(mappedBy = "reimbursement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("actedAt ASC")
    private List<ApprovalHistory> approvalHistory = new ArrayList<>();

    public Reimbursement() {
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
        this.statusChangedAt = Instant.now();
    }

    public LocalDate getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(LocalDate dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
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

    public Instant getStatusChangedAt() {
        return statusChangedAt;
    }

    public void setStatusChangedAt(Instant statusChangedAt) {
        this.statusChangedAt = statusChangedAt;
    }

    public VendorSyncStatus getVendorSyncStatus() {
        return vendorSyncStatus;
    }

    public void setVendorSyncStatus(VendorSyncStatus vendorSyncStatus) {
        this.vendorSyncStatus = vendorSyncStatus != null ? vendorSyncStatus : VendorSyncStatus.NOT_STARTED;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public List<ApprovalHistory> getApprovalHistory() {
        return approvalHistory;
    }

    public void setApprovalHistory(List<ApprovalHistory> approvalHistory) {
        this.approvalHistory = approvalHistory;
    }

    public void addHistory(ApprovalHistory entry) {
        approvalHistory.add(entry);
        entry.setReimbursement(this);
    }
}

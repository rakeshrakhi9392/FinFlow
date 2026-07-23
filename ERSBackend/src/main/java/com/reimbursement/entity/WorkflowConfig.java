package com.reimbursement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Singleton-style configurable approval rules (one active row).
 * Admins can tune escalation thresholds and which stages are required.
 */
@Entity
@Table(name = "workflow_config")
public class WorkflowConfig {

    public static final String DEFAULT_KEY = "DEFAULT";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String configKey = DEFAULT_KEY;

    /** Claims at or above this amount require senior manager review. */
    @Column(nullable = false)
    private double seniorApprovalAmountThreshold = 5000.0;

    /** When true, exceeding remaining department budget also escalates to senior manager. */
    @Column(nullable = false)
    private boolean escalateOnBudgetExceed = true;

    /** When false, manager approval skips straight to finance (no senior stage ever). */
    @Column(nullable = false)
    private boolean seniorManagerStageEnabled = true;

    /** When false, finance stage is skipped (manager/senior → vendor). */
    @Column(nullable = false)
    private boolean financeStageEnabled = true;

    @Column(length = 500)
    private String description = "Enterprise reimbursement approval workflow";

    public WorkflowConfig() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public double getSeniorApprovalAmountThreshold() {
        return seniorApprovalAmountThreshold;
    }

    public void setSeniorApprovalAmountThreshold(double seniorApprovalAmountThreshold) {
        this.seniorApprovalAmountThreshold = seniorApprovalAmountThreshold;
    }

    public boolean isEscalateOnBudgetExceed() {
        return escalateOnBudgetExceed;
    }

    public void setEscalateOnBudgetExceed(boolean escalateOnBudgetExceed) {
        this.escalateOnBudgetExceed = escalateOnBudgetExceed;
    }

    public boolean isSeniorManagerStageEnabled() {
        return seniorManagerStageEnabled;
    }

    public void setSeniorManagerStageEnabled(boolean seniorManagerStageEnabled) {
        this.seniorManagerStageEnabled = seniorManagerStageEnabled;
    }

    public boolean isFinanceStageEnabled() {
        return financeStageEnabled;
    }

    public void setFinanceStageEnabled(boolean financeStageEnabled) {
        this.financeStageEnabled = financeStageEnabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.reimbursement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * Admin update for configurable workflow / escalation rules.
 */
public class UpdateWorkflowConfigRequest {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "Threshold must be >= 0")
    private Double seniorApprovalAmountThreshold;

    private Boolean escalateOnBudgetExceed;
    private Boolean seniorManagerStageEnabled;
    private Boolean financeStageEnabled;
    private String description;

    public Double getSeniorApprovalAmountThreshold() {
        return seniorApprovalAmountThreshold;
    }

    public void setSeniorApprovalAmountThreshold(Double seniorApprovalAmountThreshold) {
        this.seniorApprovalAmountThreshold = seniorApprovalAmountThreshold;
    }

    public Boolean getEscalateOnBudgetExceed() {
        return escalateOnBudgetExceed;
    }

    public void setEscalateOnBudgetExceed(Boolean escalateOnBudgetExceed) {
        this.escalateOnBudgetExceed = escalateOnBudgetExceed;
    }

    public Boolean getSeniorManagerStageEnabled() {
        return seniorManagerStageEnabled;
    }

    public void setSeniorManagerStageEnabled(Boolean seniorManagerStageEnabled) {
        this.seniorManagerStageEnabled = seniorManagerStageEnabled;
    }

    public Boolean getFinanceStageEnabled() {
        return financeStageEnabled;
    }

    public void setFinanceStageEnabled(Boolean financeStageEnabled) {
        this.financeStageEnabled = financeStageEnabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.reimbursement.dto.response;

public class WorkflowConfigResponse {

    private Long id;
    private String configKey;
    private double seniorApprovalAmountThreshold;
    private boolean escalateOnBudgetExceed;
    private boolean seniorManagerStageEnabled;
    private boolean financeStageEnabled;
    private String description;

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

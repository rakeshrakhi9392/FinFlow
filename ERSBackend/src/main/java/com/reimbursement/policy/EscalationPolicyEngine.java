package com.reimbursement.policy;

import com.reimbursement.entity.Budget;
import com.reimbursement.entity.WorkflowConfig;
import org.springframework.stereotype.Component;

/**
 * Evaluates configurable escalation rules (amount threshold + budget overrun).
 */
@Component
public class EscalationPolicyEngine {

    public EscalationDecision evaluate(Budget budget, double requestAmount, WorkflowConfig config) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget is required for policy evaluation");
        }
        if (requestAmount <= 0) {
            throw new IllegalArgumentException("Request amount must be greater than zero");
        }
        if (config == null) {
            throw new IllegalArgumentException("Workflow config is required");
        }

        boolean byAmount = config.isSeniorManagerStageEnabled()
                && requestAmount >= config.getSeniorApprovalAmountThreshold();
        boolean byBudget = config.isSeniorManagerStageEnabled()
                && config.isEscalateOnBudgetExceed()
                && budget.wouldExceed(requestAmount);

        return new EscalationDecision(byAmount || byBudget, byAmount, byBudget);
    }

    public double calculateRemainingBudget(Budget budget) {
        return budget.remainingAmount();
    }

    public boolean exceedsRemainingBudget(Budget budget, double requestAmount) {
        return budget.wouldExceed(requestAmount);
    }

    /**
     * Result of escalation evaluation at submission time.
     */
    public record EscalationDecision(
            boolean requiresSeniorReview,
            boolean escalatedByAmount,
            boolean escalatedByBudget
    ) {
    }
}

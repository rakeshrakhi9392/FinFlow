package com.reimbursement.policy;

import com.reimbursement.entity.Budget;
import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.policy.EscalationPolicyEngine.EscalationDecision;
import org.springframework.stereotype.Component;

/**
 * Compatibility facade: budget exceed still maps to a senior path indicator
 * for older callers; new code should use {@link EscalationPolicyEngine}.
 */
@Component
public class BudgetPolicyEngine {

    private final EscalationPolicyEngine escalationPolicyEngine;

    public BudgetPolicyEngine(EscalationPolicyEngine escalationPolicyEngine) {
        this.escalationPolicyEngine = escalationPolicyEngine;
    }

    /**
     * Legacy API: returns initial queue status. All new claims start in
     * {@link ReimbursementStatus#MANAGER_REVIEW}; senior path is a flag.
     */
    public ReimbursementStatus decideApprovalPath(Budget budget, double requestAmount) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget is required for policy evaluation");
        }
        if (requestAmount <= 0) {
            throw new IllegalArgumentException("Request amount must be greater than zero");
        }
        if (escalationPolicyEngine.exceedsRemainingBudget(budget, requestAmount)) {
            return ReimbursementStatus.SENIOR_MANAGER_REVIEW;
        }
        return ReimbursementStatus.MANAGER_REVIEW;
    }

    public EscalationDecision evaluateEscalation(Budget budget, double requestAmount,
                                                 com.reimbursement.entity.WorkflowConfig config) {
        return escalationPolicyEngine.evaluate(budget, requestAmount, config);
    }

    public double calculateRemainingBudget(Budget budget) {
        return escalationPolicyEngine.calculateRemainingBudget(budget);
    }

    public boolean exceedsRemainingBudget(Budget budget, double requestAmount) {
        return escalationPolicyEngine.exceedsRemainingBudget(budget, requestAmount);
    }
}

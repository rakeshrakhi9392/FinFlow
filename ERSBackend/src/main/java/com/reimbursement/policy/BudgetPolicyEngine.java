package com.reimbursement.policy;

import com.reimbursement.entity.Budget;
import com.reimbursement.enums.ReimbursementStatus;
import org.springframework.stereotype.Component;

/**
 * Enterprise budget policy: routes submissions that would exceed remaining
 * departmental budget to senior approval instead of standard manager approval.
 */
@Component
public class BudgetPolicyEngine {

    public ReimbursementStatus decideApprovalPath(Budget budget, double requestAmount) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget is required for policy evaluation");
        }
        if (requestAmount <= 0) {
            throw new IllegalArgumentException("Request amount must be greater than zero");
        }
        double remaining = calculateRemainingBudget(budget);
        if (requestAmount > remaining) {
            return ReimbursementStatus.REQUIRES_SENIOR_APPROVAL;
        }
        return ReimbursementStatus.MANAGER_APPROVAL;
    }

    public double calculateRemainingBudget(Budget budget) {
        return budget.remainingAmount();
    }

    public boolean exceedsRemainingBudget(Budget budget, double requestAmount) {
        return budget.wouldExceed(requestAmount);
    }
}

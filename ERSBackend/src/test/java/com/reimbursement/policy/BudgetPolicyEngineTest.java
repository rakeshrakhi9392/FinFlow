package com.reimbursement.policy;

import com.reimbursement.entity.Budget;
import com.reimbursement.entity.Department;
import com.reimbursement.enums.FiscalQuarter;
import com.reimbursement.enums.ReimbursementStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BudgetPolicyEngineTest {

    private BudgetPolicyEngine policyEngine;
    private Budget budget;

    @BeforeEach
    void setUp() {
        policyEngine = new BudgetPolicyEngine();
        Department department = new Department("Sales", "SAL");
        department.setId(2L);
        budget = new Budget(department, 2026, FiscalQuarter.Q3, 10000.0);
        budget.setSpentAmount(8000.0);
    }

    @Test
    void withinBudgetRoutesToManagerApproval() {
        ReimbursementStatus status = policyEngine.decideApprovalPath(budget, 1500.0);
        assertEquals(ReimbursementStatus.MANAGER_APPROVAL, status);
    }

    @Test
    void exactRemainingStillRoutesToManagerApproval() {
        ReimbursementStatus status = policyEngine.decideApprovalPath(budget, 2000.0);
        assertEquals(ReimbursementStatus.MANAGER_APPROVAL, status);
    }

    @Test
    void exceedingRemainingRoutesToSeniorApproval() {
        ReimbursementStatus status = policyEngine.decideApprovalPath(budget, 2000.01);
        assertEquals(ReimbursementStatus.REQUIRES_SENIOR_APPROVAL, status);
    }

    @Test
    void calculateRemainingBudgetDelegatesToEntity() {
        assertEquals(2000.0, policyEngine.calculateRemainingBudget(budget), 0.001);
    }

    @Test
    void exceedsRemainingBudgetMatchesPolicyThreshold() {
        assertTrue(policyEngine.exceedsRemainingBudget(budget, 2500.0));
    }

    @Test
    void nullBudgetRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> policyEngine.decideApprovalPath(null, 100.0));
    }

    @Test
    void nonPositiveAmountRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> policyEngine.decideApprovalPath(budget, 0));
    }
}

package com.reimbursement.policy;

import com.reimbursement.entity.Budget;
import com.reimbursement.entity.Department;
import com.reimbursement.entity.WorkflowConfig;
import com.reimbursement.enums.FiscalQuarter;
import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.policy.EscalationPolicyEngine.EscalationDecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BudgetPolicyEngineTest {

    private BudgetPolicyEngine policyEngine;
    private EscalationPolicyEngine escalationPolicyEngine;
    private Budget budget;
    private WorkflowConfig config;

    @BeforeEach
    void setUp() {
        escalationPolicyEngine = new EscalationPolicyEngine();
        policyEngine = new BudgetPolicyEngine(escalationPolicyEngine);
        Department department = new Department("Sales", "SAL");
        department.setId(2L);
        budget = new Budget(department, 2026, FiscalQuarter.Q3, 10000.0);
        budget.setSpentAmount(8000.0);
        config = new WorkflowConfig();
        config.setSeniorApprovalAmountThreshold(5000.0);
        config.setEscalateOnBudgetExceed(true);
        config.setSeniorManagerStageEnabled(true);
    }

    @Test
    void withinBudgetRoutesToManagerReview() {
        ReimbursementStatus status = policyEngine.decideApprovalPath(budget, 1500.0);
        assertEquals(ReimbursementStatus.MANAGER_REVIEW, status);
    }

    @Test
    void exactRemainingStillRoutesToManagerReview() {
        ReimbursementStatus status = policyEngine.decideApprovalPath(budget, 2000.0);
        assertEquals(ReimbursementStatus.MANAGER_REVIEW, status);
    }

    @Test
    void exceedingRemainingRoutesToSeniorPath() {
        ReimbursementStatus status = policyEngine.decideApprovalPath(budget, 2000.01);
        assertEquals(ReimbursementStatus.SENIOR_MANAGER_REVIEW, status);
    }

    @Test
    void escalationByAmountUsesConfigurableThreshold() {
        EscalationDecision decision = escalationPolicyEngine.evaluate(budget, 5000.0, config);
        assertTrue(decision.requiresSeniorReview());
        assertTrue(decision.escalatedByAmount());
    }

    @Test
    void escalationByBudgetWhenExceedingRemaining() {
        EscalationDecision decision = escalationPolicyEngine.evaluate(budget, 2500.0, config);
        assertTrue(decision.requiresSeniorReview());
        assertTrue(decision.escalatedByBudget());
        assertFalse(decision.escalatedByAmount());
    }

    @Test
    void noEscalationWhenUnderThresholdAndBudget() {
        EscalationDecision decision = escalationPolicyEngine.evaluate(budget, 500.0, config);
        assertFalse(decision.requiresSeniorReview());
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

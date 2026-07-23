package com.reimbursement.entity;

import com.reimbursement.enums.FiscalQuarter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BudgetCalculationTest {

    private Budget budget;

    @BeforeEach
    void setUp() {
        Department department = new Department("Engineering", "ENG");
        department.setId(1L);
        budget = new Budget(department, 2026, FiscalQuarter.Q3, 10000.0);
        budget.setSpentAmount(2500.0);
    }

    @Test
    void remainingBudgetIsAllocatedMinusSpent() {
        assertEquals(7500.0, budget.remainingAmount(), 0.001);
    }

    @Test
    void utilizationPercentReflectsSpendRatio() {
        assertEquals(25.0, budget.utilizationPercent(), 0.001);
    }

    @Test
    void utilizationIsZeroWhenAllocatedIsZero() {
        budget.setAllocatedAmount(0);
        budget.setSpentAmount(0);
        assertEquals(0.0, budget.utilizationPercent(), 0.001);
    }

    @Test
    void wouldExceedWhenRequestGreaterThanRemaining() {
        assertTrue(budget.wouldExceed(7500.01));
        assertFalse(budget.wouldExceed(7500.0));
        assertFalse(budget.wouldExceed(100.0));
    }

    @Test
    void applySpendIncrementsSpentAmount() {
        budget.applySpend(500.0);
        assertEquals(3000.0, budget.getSpentAmount(), 0.001);
        assertEquals(7000.0, budget.remainingAmount(), 0.001);
    }

    @Test
    void applySpendRejectsNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class, () -> budget.applySpend(0));
        assertThrows(IllegalArgumentException.class, () -> budget.applySpend(-10));
    }
}

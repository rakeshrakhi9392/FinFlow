package com.reimbursement.service;

import com.reimbursement.entity.Budget;
import com.reimbursement.entity.Department;
import com.reimbursement.enums.FiscalQuarter;
import com.reimbursement.repository.BudgetRepository;
import com.reimbursement.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Import(BudgetService.class)
class BudgetOptimisticLockingTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BudgetService budgetService;

    private Long budgetId;

    @BeforeEach
    void setUp() {
        Department department = departmentRepository.save(new Department("Operations", "OPS"));
        Budget budget = new Budget(department, 2026, FiscalQuarter.Q3, 20000.0);
        budget = budgetRepository.saveAndFlush(budget);
        budgetId = budget.getId();
        entityManager.clear();
    }

    @Test
    void applySpendAtomicallyIncrementsSpentAmount() {
        Budget updated = budgetService.applySpendAtomically(budgetId, 1500.0);
        assertEquals(1500.0, updated.getSpentAmount(), 0.001);
        assertEquals(18500.0, updated.remainingAmount(), 0.001);
        assertTrue(updated.getVersion() >= 1L);
    }

    @Test
    void concurrentStaleVersionUpdateFailsOptimisticLock() {
        Budget first = budgetRepository.findById(budgetId).orElseThrow();
        Budget second = budgetRepository.findById(budgetId).orElseThrow();
        entityManager.detach(first);
        entityManager.detach(second);

        first.applySpend(1000.0);
        budgetRepository.saveAndFlush(first);
        entityManager.clear();

        second.applySpend(500.0);
        assertThrows(ObjectOptimisticLockingFailureException.class,
                () -> budgetRepository.saveAndFlush(second));
    }

    @Test
    void sequentialAtomicSpendsAccumulateCorrectly() {
        budgetService.applySpendAtomically(budgetId, 1000.0);
        entityManager.clear();
        Budget second = budgetService.applySpendAtomically(budgetId, 500.0);
        assertEquals(1500.0, second.getSpentAmount(), 0.001);
    }
}

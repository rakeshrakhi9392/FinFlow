package com.reimbursement.service;

import com.reimbursement.entity.Budget;
import com.reimbursement.entity.Department;
import com.reimbursement.enums.FiscalQuarter;
import com.reimbursement.exception.OptimisticLockConflictException;
import com.reimbursement.exception.ResourceNotFoundException;
import com.reimbursement.repository.BudgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceOptimisticLockUnitTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetService budgetService;

    @Test
    void applySpendAtomicallyMapsOptimisticFailureToConflict() {
        Department department = new Department("Finance", "FIN");
        department.setId(1L);
        Budget budget = new Budget(department, 2026, FiscalQuarter.Q3, 10000.0);
        budget.setId(10L);
        budget.setVersion(1L);

        when(budgetRepository.findById(10L)).thenReturn(Optional.of(budget));
        when(budgetRepository.saveAndFlush(any(Budget.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Budget.class, 10L));

        OptimisticLockConflictException ex = assertThrows(
                OptimisticLockConflictException.class,
                () -> budgetService.applySpendAtomically(10L, 250.0));
        assertEquals("Concurrent budget update detected. Please retry approval.", ex.getMessage());
    }

    @Test
    void applySpendAtomicallySucceedsAndReturnsUpdatedBudget() {
        Department department = new Department("Finance", "FIN");
        department.setId(1L);
        Budget budget = new Budget(department, 2026, FiscalQuarter.Q3, 10000.0);
        budget.setId(10L);
        budget.setVersion(0L);

        when(budgetRepository.findById(10L)).thenReturn(Optional.of(budget));
        when(budgetRepository.saveAndFlush(any(Budget.class))).thenAnswer(invocation -> {
            Budget saved = invocation.getArgument(0);
            saved.setVersion(1L);
            return saved;
        });

        Budget result = budgetService.applySpendAtomically(10L, 400.0);
        assertEquals(400.0, result.getSpentAmount(), 0.001);
        assertEquals(1L, result.getVersion());
    }

    @Test
    void applySpendAtomicallyFailsWhenBudgetMissing() {
        when(budgetRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> budgetService.applySpendAtomically(99L, 10.0));
    }
}

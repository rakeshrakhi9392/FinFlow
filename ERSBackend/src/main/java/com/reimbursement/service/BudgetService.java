package com.reimbursement.service;

import com.reimbursement.dto.response.BudgetSummaryResponse;
import com.reimbursement.dto.response.DepartmentBudgetResponse;
import com.reimbursement.entity.Budget;
import com.reimbursement.entity.Department;
import com.reimbursement.enums.FiscalQuarter;
import com.reimbursement.exception.OptimisticLockConflictException;
import com.reimbursement.exception.ResourceNotFoundException;
import com.reimbursement.repository.BudgetRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public double calculateRemainingBudget(Budget budget) {
        return budget.remainingAmount();
    }

    public double calculateUtilizationPercent(Budget budget) {
        return budget.utilizationPercent();
    }

    public Budget requireBudgetForDepartment(Department department, LocalDate asOf) {
        int year = FiscalQuarter.fiscalYearOf(asOf);
        FiscalQuarter quarter = FiscalQuarter.fromDate(asOf);
        return budgetRepository.findByDepartmentIdAndFiscalYearAndQuarter(department.getId(), year, quarter)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No budget found for department " + department.getCode()
                                + " in " + year + " " + quarter));
    }

    public Budget findById(Long budgetId) {
        return budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found: " + budgetId));
    }

    /**
     * Atomically increments spentAmount using JPA @Version optimistic locking.
     * Concurrent approvers that lose the version check receive a conflict error.
     */
    @Transactional
    public Budget applySpendAtomically(Long budgetId, double amount) {
        try {
            Budget budget = findById(budgetId);
            budget.applySpend(amount);
            return budgetRepository.saveAndFlush(budget);
        } catch (OptimisticLockingFailureException ex) {
            throw new OptimisticLockConflictException(
                    "Concurrent budget update detected. Please retry approval.", ex);
        }
    }

    public BudgetSummaryResponse getCurrentPeriodSummary() {
        LocalDate today = LocalDate.now();
        return getSummary(FiscalQuarter.fiscalYearOf(today), FiscalQuarter.fromDate(today));
    }

    public BudgetSummaryResponse getSummary(int fiscalYear, FiscalQuarter quarter) {
        List<Budget> budgets = budgetRepository.findAllWithDepartmentForPeriod(fiscalYear, quarter);
        List<DepartmentBudgetResponse> departmentRows = budgets.stream()
                .map(this::toDepartmentResponse)
                .collect(Collectors.toList());

        double totalAllocated = departmentRows.stream().mapToDouble(DepartmentBudgetResponse::getAllocatedAmount).sum();
        double totalSpent = departmentRows.stream().mapToDouble(DepartmentBudgetResponse::getSpentAmount).sum();
        double remaining = totalAllocated - totalSpent;
        double utilization = totalAllocated <= 0 ? 0.0 : (totalSpent / totalAllocated) * 100.0;

        BudgetSummaryResponse summary = new BudgetSummaryResponse();
        summary.setFiscalYear(fiscalYear);
        summary.setQuarter(quarter);
        summary.setTotalAllocated(round2(totalAllocated));
        summary.setTotalSpent(round2(totalSpent));
        summary.setRemainingBudget(round2(remaining));
        summary.setUtilizationPercent(round2(utilization));
        summary.setDepartments(departmentRows);
        return summary;
    }

    public List<DepartmentBudgetResponse> getDepartmentSpend(int fiscalYear, FiscalQuarter quarter) {
        return budgetRepository.findAllWithDepartmentForPeriod(fiscalYear, quarter).stream()
                .map(this::toDepartmentResponse)
                .collect(Collectors.toList());
    }

    public DepartmentBudgetResponse getDepartmentBudget(Long departmentId, int fiscalYear, FiscalQuarter quarter) {
        Budget budget = budgetRepository.findByDepartmentIdAndFiscalYearAndQuarter(departmentId, fiscalYear, quarter)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Budget not found for department " + departmentId + " " + fiscalYear + " " + quarter));
        return toDepartmentResponse(budget);
    }

    private DepartmentBudgetResponse toDepartmentResponse(Budget budget) {
        Department department = budget.getDepartment();
        return new DepartmentBudgetResponse(
                budget.getId(),
                department.getId(),
                department.getName(),
                department.getCode(),
                budget.getFiscalYear(),
                budget.getQuarter(),
                round2(budget.getAllocatedAmount()),
                round2(budget.getSpentAmount()),
                round2(budget.remainingAmount()),
                round2(budget.utilizationPercent())
        );
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

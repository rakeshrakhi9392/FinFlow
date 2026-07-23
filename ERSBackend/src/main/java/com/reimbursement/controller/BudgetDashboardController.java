package com.reimbursement.controller;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.dto.response.BudgetSummaryResponse;
import com.reimbursement.dto.response.DepartmentBudgetResponse;
import com.reimbursement.enums.FiscalQuarter;
import com.reimbursement.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(AppConstants.API_BUDGETS)
@CrossOrigin(origins = { AppConstants.CORS_ORIGIN_LOCAL }, allowedHeaders = "*", allowCredentials = "true")
public class BudgetDashboardController {

    private final BudgetService budgetService;

    public BudgetDashboardController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/summary")
    public ResponseEntity<BudgetSummaryResponse> getBudgetSummary(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) FiscalQuarter quarter) {
        LocalDate today = LocalDate.now();
        int fiscalYear = year != null ? year : FiscalQuarter.fiscalYearOf(today);
        FiscalQuarter fiscalQuarter = quarter != null ? quarter : FiscalQuarter.fromDate(today);
        return ResponseEntity.ok(budgetService.getSummary(fiscalYear, fiscalQuarter));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentBudgetResponse>> getDepartmentSpend(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) FiscalQuarter quarter) {
        LocalDate today = LocalDate.now();
        int fiscalYear = year != null ? year : FiscalQuarter.fiscalYearOf(today);
        FiscalQuarter fiscalQuarter = quarter != null ? quarter : FiscalQuarter.fromDate(today);
        return ResponseEntity.ok(budgetService.getDepartmentSpend(fiscalYear, fiscalQuarter));
    }

    @GetMapping("/departments/{departmentId}")
    public ResponseEntity<DepartmentBudgetResponse> getDepartmentBudget(
            @PathVariable Long departmentId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) FiscalQuarter quarter) {
        LocalDate today = LocalDate.now();
        int fiscalYear = year != null ? year : FiscalQuarter.fiscalYearOf(today);
        FiscalQuarter fiscalQuarter = quarter != null ? quarter : FiscalQuarter.fromDate(today);
        return ResponseEntity.ok(budgetService.getDepartmentBudget(departmentId, fiscalYear, fiscalQuarter));
    }
}

package com.reimbursement.dto.response;

import com.reimbursement.enums.FiscalQuarter;

import java.util.ArrayList;
import java.util.List;

public class BudgetSummaryResponse {

    private int fiscalYear;
    private FiscalQuarter quarter;
    private double totalAllocated;
    private double totalSpent;
    private double remainingBudget;
    private double utilizationPercent;
    private List<DepartmentBudgetResponse> departments = new ArrayList<>();

    public BudgetSummaryResponse() {
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(int fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public FiscalQuarter getQuarter() {
        return quarter;
    }

    public void setQuarter(FiscalQuarter quarter) {
        this.quarter = quarter;
    }

    public double getTotalAllocated() {
        return totalAllocated;
    }

    public void setTotalAllocated(double totalAllocated) {
        this.totalAllocated = totalAllocated;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public double getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(double remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public double getUtilizationPercent() {
        return utilizationPercent;
    }

    public void setUtilizationPercent(double utilizationPercent) {
        this.utilizationPercent = utilizationPercent;
    }

    public List<DepartmentBudgetResponse> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentBudgetResponse> departments) {
        this.departments = departments;
    }
}

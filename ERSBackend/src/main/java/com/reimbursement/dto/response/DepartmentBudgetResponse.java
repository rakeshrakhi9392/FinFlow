package com.reimbursement.dto.response;

import com.reimbursement.enums.FiscalQuarter;

public class DepartmentBudgetResponse {

    private Long budgetId;
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private int fiscalYear;
    private FiscalQuarter quarter;
    private double allocatedAmount;
    private double spentAmount;
    private double remainingBudget;
    private double utilizationPercent;

    public DepartmentBudgetResponse() {
    }

    public DepartmentBudgetResponse(Long budgetId, Long departmentId, String departmentName, String departmentCode,
                                    int fiscalYear, FiscalQuarter quarter, double allocatedAmount,
                                    double spentAmount, double remainingBudget, double utilizationPercent) {
        this.budgetId = budgetId;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.fiscalYear = fiscalYear;
        this.quarter = quarter;
        this.allocatedAmount = allocatedAmount;
        this.spentAmount = spentAmount;
        this.remainingBudget = remainingBudget;
        this.utilizationPercent = utilizationPercent;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
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

    public double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
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
}

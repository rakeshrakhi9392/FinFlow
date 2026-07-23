package com.reimbursement.entity;

import com.reimbursement.enums.FiscalQuarter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(
        name = "budgets",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_budget_department_year_quarter",
                columnNames = {"department_id", "fiscal_year", "quarter"}
        )
)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "fiscal_year", nullable = false)
    private int fiscalYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FiscalQuarter quarter;

    @Column(name = "allocated_amount", nullable = false)
    private double allocatedAmount;

    @Column(name = "spent_amount", nullable = false)
    private double spentAmount;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public Budget() {
    }

    public Budget(Department department, int fiscalYear, FiscalQuarter quarter, double allocatedAmount) {
        this.department = department;
        this.fiscalYear = fiscalYear;
        this.quarter = quarter;
        this.allocatedAmount = allocatedAmount;
        this.spentAmount = 0.0;
    }

    public double remainingAmount() {
        return allocatedAmount - spentAmount;
    }

    public double utilizationPercent() {
        if (allocatedAmount <= 0) {
            return 0.0;
        }
        return (spentAmount / allocatedAmount) * 100.0;
    }

    /**
     * Atomically apply spend against this budget instance.
     * Persistence must use optimistic locking (@Version) to serialize concurrent updates.
     */
    public void applySpend(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Spend amount must be greater than zero");
        }
        this.spentAmount += amount;
    }

    public boolean wouldExceed(double amount) {
        return amount > remainingAmount();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

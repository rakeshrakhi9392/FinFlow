package com.reimbursement.repository;

import com.reimbursement.entity.Budget;
import com.reimbursement.enums.FiscalQuarter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByDepartmentIdAndFiscalYearAndQuarter(
            Long departmentId, int fiscalYear, FiscalQuarter quarter);

    List<Budget> findByFiscalYearAndQuarter(int fiscalYear, FiscalQuarter quarter);

    @Query("SELECT b FROM Budget b JOIN FETCH b.department WHERE b.fiscalYear = :fiscalYear AND b.quarter = :quarter")
    List<Budget> findAllWithDepartmentForPeriod(int fiscalYear, FiscalQuarter quarter);

    @Query("SELECT b FROM Budget b JOIN FETCH b.department")
    List<Budget> findAllWithDepartment();
}

package com.reimbursement.mapper;

import com.reimbursement.dto.request.CreateReimbursementRequest;
import com.reimbursement.dto.response.ReimbursementResponse;
import com.reimbursement.entity.Budget;
import com.reimbursement.entity.Department;
import com.reimbursement.entity.ExpenseCategory;
import com.reimbursement.entity.Reimbursement;
import com.reimbursement.entity.User;
import com.reimbursement.enums.ReimbursementStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReimbursementMapper {

    public Reimbursement toEntity(CreateReimbursementRequest request, User user,
                                  Department department, ExpenseCategory category,
                                  Budget budget, ReimbursementStatus status) {
        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setAmount(request.getAmount());
        reimbursement.setDescription(request.getDescription());
        reimbursement.setDateSubmitted(LocalDate.now());
        reimbursement.setStatus(status);
        reimbursement.setUser(user);
        reimbursement.setDepartment(department);
        reimbursement.setExpenseCategory(category);
        reimbursement.setBudget(budget);
        return reimbursement;
    }

    public ReimbursementResponse toResponse(Reimbursement reimbursement) {
        ReimbursementResponse response = new ReimbursementResponse(
                reimbursement.getReimbursementId(),
                reimbursement.getAmount(),
                reimbursement.getDescription(),
                reimbursement.getStatus(),
                reimbursement.getDateSubmitted()
        );
        if (reimbursement.getDepartment() != null) {
            response.setDepartmentId(reimbursement.getDepartment().getId());
            response.setDepartmentName(reimbursement.getDepartment().getName());
        }
        if (reimbursement.getExpenseCategory() != null) {
            response.setCategoryId(reimbursement.getExpenseCategory().getId());
            response.setCategoryName(reimbursement.getExpenseCategory().getName());
        }
        if (reimbursement.getBudget() != null) {
            response.setBudgetId(reimbursement.getBudget().getId());
            response.setRemainingBudgetAtSubmit(reimbursement.getBudget().remainingAmount());
        }
        return response;
    }

    public List<ReimbursementResponse> toResponseList(List<Reimbursement> reimbursements) {
        return reimbursements.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

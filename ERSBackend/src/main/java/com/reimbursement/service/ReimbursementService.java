package com.reimbursement.service;

import com.reimbursement.dto.request.CreateReimbursementRequest;
import com.reimbursement.dto.response.ReimbursementResponse;
import com.reimbursement.entity.Budget;
import com.reimbursement.entity.Department;
import com.reimbursement.entity.ExpenseCategory;
import com.reimbursement.entity.Reimbursement;
import com.reimbursement.entity.User;
import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.exception.BadRequestException;
import com.reimbursement.exception.ResourceNotFoundException;
import com.reimbursement.mapper.ReimbursementMapper;
import com.reimbursement.policy.BudgetPolicyEngine;
import com.reimbursement.policy.ReimbursementPolicy;
import com.reimbursement.repository.DepartmentRepository;
import com.reimbursement.repository.ExpenseCategoryRepository;
import com.reimbursement.repository.ReimbursementRepository;
import com.reimbursement.repository.UserRepository;
import com.reimbursement.workflow.ReimbursementWorkflow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReimbursementService {

    private final ReimbursementRepository reimbursementRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ReimbursementMapper reimbursementMapper;
    private final ReimbursementWorkflow reimbursementWorkflow;
    private final ReimbursementPolicy reimbursementPolicy;
    private final BudgetService budgetService;
    private final BudgetPolicyEngine budgetPolicyEngine;

    public ReimbursementService(ReimbursementRepository reimbursementRepository,
                                UserRepository userRepository,
                                DepartmentRepository departmentRepository,
                                ExpenseCategoryRepository expenseCategoryRepository,
                                ReimbursementMapper reimbursementMapper,
                                ReimbursementWorkflow reimbursementWorkflow,
                                ReimbursementPolicy reimbursementPolicy,
                                BudgetService budgetService,
                                BudgetPolicyEngine budgetPolicyEngine) {
        this.reimbursementRepository = reimbursementRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.reimbursementMapper = reimbursementMapper;
        this.reimbursementWorkflow = reimbursementWorkflow;
        this.reimbursementPolicy = reimbursementPolicy;
        this.budgetService = budgetService;
        this.budgetPolicyEngine = budgetPolicyEngine;
    }

    public List<ReimbursementResponse> findAll() {
        return reimbursementMapper.toResponseList(reimbursementRepository.findAll());
    }

    public List<ReimbursementResponse> findByUserId(int userId) {
        return reimbursementMapper.toResponseList(reimbursementRepository.findByUserUserId(userId));
    }

    @Transactional
    public ReimbursementResponse createReimbursement(CreateReimbursementRequest request, Integer userId) {
        reimbursementPolicy.requireAuthenticated(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " does not exist"));

        Department department = resolveDepartment(request, user);
        ExpenseCategory category = resolveCategory(request);
        LocalDate today = LocalDate.now();
        Budget budget = budgetService.requireBudgetForDepartment(department, today);

        double remaining = budgetPolicyEngine.calculateRemainingBudget(budget);
        ReimbursementStatus status = budgetPolicyEngine.decideApprovalPath(budget, request.getAmount());

        Reimbursement entity = reimbursementMapper.toEntity(
                request, user, department, category, budget, status);
        Reimbursement saved = reimbursementRepository.save(entity);

        ReimbursementResponse response = reimbursementMapper.toResponse(saved);
        response.setRemainingBudgetAtSubmit(remaining);
        return response;
    }

    @Transactional
    public ReimbursementResponse approveReimbursement(int id) {
        Reimbursement reimbursement = findEntityById(id);
        reimbursementWorkflow.approve(reimbursement);

        if (reimbursement.getBudget() == null) {
            throw new BadRequestException("Reimbursement is not linked to a budget");
        }
        budgetService.applySpendAtomically(reimbursement.getBudget().getId(), reimbursement.getAmount());
        return reimbursementMapper.toResponse(reimbursementRepository.save(reimbursement));
    }

    @Transactional
    public ReimbursementResponse denyReimbursement(int id) {
        Reimbursement reimbursement = findEntityById(id);
        reimbursementWorkflow.deny(reimbursement);
        return reimbursementMapper.toResponse(reimbursementRepository.save(reimbursement));
    }

    private Department resolveDepartment(CreateReimbursementRequest request, User user) {
        if (request.getDepartmentId() != null) {
            return departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department not found: " + request.getDepartmentId()));
        }
        if (user.getDepartment() != null) {
            return user.getDepartment();
        }
        throw new BadRequestException("User has no department assigned; provide departmentId on the request");
    }

    private ExpenseCategory resolveCategory(CreateReimbursementRequest request) {
        if (request.getCategoryId() != null) {
            return expenseCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Expense category not found: " + request.getCategoryId()));
        }
        return expenseCategoryRepository.findByCode("GENERAL")
                .orElseThrow(() -> new ResourceNotFoundException("Default expense category GENERAL is not seeded"));
    }

    private Reimbursement findEntityById(int id) {
        return reimbursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reimbursement request not found"));
    }
}

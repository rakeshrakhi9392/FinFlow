package com.reimbursement.service;

import com.reimbursement.dto.request.ApprovalDecisionRequest;
import com.reimbursement.dto.request.CreateReimbursementRequest;
import com.reimbursement.dto.response.ReimbursementResponse;
import com.reimbursement.entity.ApprovalHistory;
import com.reimbursement.entity.Budget;
import com.reimbursement.entity.Department;
import com.reimbursement.entity.ExpenseCategory;
import com.reimbursement.entity.Reimbursement;
import com.reimbursement.entity.User;
import com.reimbursement.entity.WorkflowConfig;
import com.reimbursement.enums.ApprovalAction;
import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.enums.UserRole;
import com.reimbursement.exception.BadRequestException;
import com.reimbursement.exception.ResourceNotFoundException;
import com.reimbursement.mapper.ReimbursementMapper;
import com.reimbursement.policy.EscalationPolicyEngine;
import com.reimbursement.policy.EscalationPolicyEngine.EscalationDecision;
import com.reimbursement.policy.ReimbursementPolicy;
import com.reimbursement.repository.DepartmentRepository;
import com.reimbursement.repository.ExpenseCategoryRepository;
import com.reimbursement.repository.ReimbursementRepository;
import com.reimbursement.repository.UserRepository;
import com.reimbursement.workflow.ReimbursementWorkflow;
import com.reimbursement.workflow.ReimbursementWorkflow.TransitionResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    private final EscalationPolicyEngine escalationPolicyEngine;
    private final WorkflowConfigService workflowConfigService;

    public ReimbursementService(ReimbursementRepository reimbursementRepository,
                                UserRepository userRepository,
                                DepartmentRepository departmentRepository,
                                ExpenseCategoryRepository expenseCategoryRepository,
                                ReimbursementMapper reimbursementMapper,
                                ReimbursementWorkflow reimbursementWorkflow,
                                ReimbursementPolicy reimbursementPolicy,
                                BudgetService budgetService,
                                EscalationPolicyEngine escalationPolicyEngine,
                                WorkflowConfigService workflowConfigService) {
        this.reimbursementRepository = reimbursementRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.reimbursementMapper = reimbursementMapper;
        this.reimbursementWorkflow = reimbursementWorkflow;
        this.reimbursementPolicy = reimbursementPolicy;
        this.budgetService = budgetService;
        this.escalationPolicyEngine = escalationPolicyEngine;
        this.workflowConfigService = workflowConfigService;
    }

    public List<ReimbursementResponse> findAll(UserRole viewerRole) {
        return reimbursementMapper.toResponseList(reimbursementRepository.findAll(), viewerRole);
    }

    public List<ReimbursementResponse> findByUserId(int userId, UserRole viewerRole) {
        return reimbursementMapper.toResponseList(reimbursementRepository.findByUserUserId(userId), viewerRole);
    }

    public List<ReimbursementResponse> findQueueForRole(UserRole role) {
        return reimbursementMapper.toResponseList(
                reimbursementRepository.findByStatusIn(role.actionableStatuses()), role);
    }

    public ReimbursementResponse findById(int id, UserRole viewerRole) {
        Reimbursement entity = reimbursementRepository.findByIdWithHistory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reimbursement request not found"));
        return reimbursementMapper.toResponse(entity, viewerRole);
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
        WorkflowConfig config = workflowConfigService.getActiveConfig();

        double remaining = escalationPolicyEngine.calculateRemainingBudget(budget);
        EscalationDecision escalation = escalationPolicyEngine.evaluate(budget, request.getAmount(), config);

        Reimbursement entity = reimbursementMapper.toEntity(
                request, user, department, category, budget,
                ReimbursementStatus.SUBMITTED,
                escalation.requiresSeniorReview(),
                escalation.escalatedByAmount(),
                escalation.escalatedByBudget());

        TransitionResult submit = reimbursementWorkflow.onSubmit(entity);
        recordHistory(entity, user, submit, buildSubmitComment(escalation, config), user.getRole());

        Reimbursement saved = reimbursementRepository.save(entity);
        ReimbursementResponse response = reimbursementMapper.toResponse(saved, UserRole.fromValue(user.getRole()));
        response.setRemainingBudgetAtSubmit(remaining);
        return response;
    }

    @Transactional
    public ReimbursementResponse approveReimbursement(int id, Integer actorId, ApprovalDecisionRequest decision) {
        return resolveDecision(id, actorId, decision, true);
    }

    @Transactional
    public ReimbursementResponse denyReimbursement(int id, Integer actorId, ApprovalDecisionRequest decision) {
        return resolveDecision(id, actorId, decision, false);
    }

    @Transactional
    public ReimbursementResponse markPaid(int id, Integer actorId, ApprovalDecisionRequest decision) {
        User actor = requireActor(actorId);
        Reimbursement reimbursement = findEntityById(id);
        TransitionResult result = reimbursementWorkflow.markPaid(reimbursement, actor);
        recordHistory(reimbursement, actor, result, commentOf(decision), actor.getRole());
        return reimbursementMapper.toResponse(
                reimbursementRepository.save(reimbursement), UserRole.fromValue(actor.getRole()));
    }

    /** Legacy no-comment approve for older clients. */
    @Transactional
    public ReimbursementResponse approveReimbursement(int id) {
        throw new BadRequestException("Authenticated actor required for approval");
    }

    @Transactional
    public ReimbursementResponse denyReimbursement(int id) {
        throw new BadRequestException("Authenticated actor required for denial");
    }

    private ReimbursementResponse resolveDecision(int id, Integer actorId, ApprovalDecisionRequest decision,
                                                  boolean approve) {
        User actor = requireActor(actorId);
        Reimbursement reimbursement = findEntityById(id);
        WorkflowConfig config = workflowConfigService.getActiveConfig();

        TransitionResult result = approve
                ? reimbursementWorkflow.approve(reimbursement, actor, config)
                : reimbursementWorkflow.deny(reimbursement, actor);

        recordHistory(reimbursement, actor, result, commentOf(decision), actor.getRole());

        if (approve && result.to() == ReimbursementStatus.VENDOR_PROCESSING) {
            if (reimbursement.getBudget() == null) {
                throw new BadRequestException("Reimbursement is not linked to a budget");
            }
            budgetService.applySpendAtomically(reimbursement.getBudget().getId(), reimbursement.getAmount());
        }

        return reimbursementMapper.toResponse(
                reimbursementRepository.save(reimbursement), UserRole.fromValue(actor.getRole()));
    }

    private void recordHistory(Reimbursement reimbursement, User actor, TransitionResult result,
                               String comment, String actorRole) {
        ApprovalHistory entry = new ApprovalHistory();
        entry.setActor(actor);
        entry.setAction(result.action());
        entry.setFromStatus(result.from());
        entry.setToStatus(result.to());
        entry.setComment(comment);
        entry.setActedAt(Instant.now());
        entry.setActorRole(actorRole);
        if (result.action() == ApprovalAction.APPROVED
                && result.to() == ReimbursementStatus.SENIOR_MANAGER_REVIEW) {
            entry.setAction(ApprovalAction.ESCALATED);
        }
        reimbursement.addHistory(entry);
    }

    private String buildSubmitComment(EscalationDecision escalation, WorkflowConfig config) {
        if (!escalation.requiresSeniorReview()) {
            return "Submitted for manager review";
        }
        StringBuilder sb = new StringBuilder("Submitted with senior escalation");
        if (escalation.escalatedByAmount()) {
            sb.append(" (amount ≥ $").append(config.getSeniorApprovalAmountThreshold()).append(")");
        }
        if (escalation.escalatedByBudget()) {
            sb.append(" (exceeds remaining budget)");
        }
        return sb.toString();
    }

    private String commentOf(ApprovalDecisionRequest decision) {
        return decision != null ? decision.getComment() : null;
    }

    private User requireActor(Integer actorId) {
        reimbursementPolicy.requireAuthenticated(actorId);
        return userRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor user not found: " + actorId));
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
        return reimbursementRepository.findByIdWithHistory(id)
                .orElseGet(() -> reimbursementRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Reimbursement request not found")));
    }
}

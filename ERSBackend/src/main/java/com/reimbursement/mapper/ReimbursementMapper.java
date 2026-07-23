package com.reimbursement.mapper;

import com.reimbursement.dto.request.CreateReimbursementRequest;
import com.reimbursement.dto.response.ApprovalHistoryResponse;
import com.reimbursement.dto.response.ReimbursementResponse;
import com.reimbursement.dto.response.WorkflowTimelineResponse;
import com.reimbursement.entity.ApprovalHistory;
import com.reimbursement.entity.Budget;
import com.reimbursement.entity.Department;
import com.reimbursement.entity.ExpenseCategory;
import com.reimbursement.entity.Reimbursement;
import com.reimbursement.entity.User;
import com.reimbursement.enums.ApprovalAction;
import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.enums.UserRole;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReimbursementMapper {

    public Reimbursement toEntity(CreateReimbursementRequest request, User user,
                                  Department department, ExpenseCategory category,
                                  Budget budget, ReimbursementStatus status,
                                  boolean requiresSenior, boolean byAmount, boolean byBudget) {
        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setAmount(request.getAmount());
        reimbursement.setDescription(request.getDescription());
        reimbursement.setDateSubmitted(LocalDate.now());
        reimbursement.setStatus(status);
        reimbursement.setRequiresSeniorReview(requiresSenior);
        reimbursement.setEscalatedByAmount(byAmount);
        reimbursement.setEscalatedByBudget(byBudget);
        reimbursement.setUser(user);
        reimbursement.setDepartment(department);
        reimbursement.setExpenseCategory(category);
        reimbursement.setBudget(budget);
        return reimbursement;
    }

    public ReimbursementResponse toResponse(Reimbursement reimbursement) {
        return toResponse(reimbursement, null);
    }

    public ReimbursementResponse toResponse(Reimbursement reimbursement, UserRole viewerRole) {
        ReimbursementResponse response = new ReimbursementResponse(
                reimbursement.getReimbursementId(),
                reimbursement.getAmount(),
                reimbursement.getDescription(),
                reimbursement.getStatus(),
                reimbursement.getDateSubmitted()
        );
        response.setStatusChangedAt(reimbursement.getStatusChangedAt());
        response.setRequiresSeniorReview(reimbursement.isRequiresSeniorReview());
        response.setEscalatedByAmount(reimbursement.isEscalatedByAmount());
        response.setEscalatedByBudget(reimbursement.isEscalatedByBudget());

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
        if (reimbursement.getUser() != null) {
            response.setSubmitterId(reimbursement.getUser().getUserId());
            response.setSubmitterUsername(reimbursement.getUser().getUsername());
        }

        List<ApprovalHistory> history = reimbursement.getApprovalHistory() == null
                ? List.of()
                : reimbursement.getApprovalHistory().stream()
                .sorted(Comparator.comparing(ApprovalHistory::getActedAt))
                .collect(Collectors.toList());
        response.setApprovalHistory(history.stream().map(this::toHistoryResponse).collect(Collectors.toList()));
        response.setTimeline(buildTimeline(reimbursement, history));

        if (viewerRole != null) {
            response.setAllowedActions(resolveAllowedActions(reimbursement, viewerRole));
        }

        response.setVendorSyncStatus(reimbursement.getVendorSyncStatus());
        response.setVendorSystem(reimbursement.getVendorSystem());
        response.setAccountingDocument(reimbursement.getAccountingDocument());
        response.setVendorReferenceNumber(reimbursement.getVendorReferenceNumber());
        response.setVendorPostingDate(reimbursement.getVendorPostingDate());
        response.setVendorId(reimbursement.getVendorId());
        response.setVendorPaymentStatus(reimbursement.getVendorPaymentStatus());
        response.setLastVendorSyncAt(reimbursement.getLastVendorSyncAt());
        response.setVendorSyncAttempts(reimbursement.getVendorSyncAttempts());
        response.setVendorResponse(reimbursement.getVendorResponse());
        response.setVendorErrorCode(reimbursement.getVendorErrorCode());
        response.setVendorErrorMessage(reimbursement.getVendorErrorMessage());
        return response;
    }

    public List<ReimbursementResponse> toResponseList(List<Reimbursement> reimbursements) {
        return toResponseList(reimbursements, null);
    }

    public List<ReimbursementResponse> toResponseList(List<Reimbursement> reimbursements, UserRole viewerRole) {
        return reimbursements.stream()
                .map(r -> toResponse(r, viewerRole))
                .collect(Collectors.toList());
    }

    public ApprovalHistoryResponse toHistoryResponse(ApprovalHistory entry) {
        ApprovalHistoryResponse response = new ApprovalHistoryResponse();
        response.setId(entry.getId());
        response.setAction(entry.getAction());
        response.setFromStatus(entry.getFromStatus());
        response.setToStatus(entry.getToStatus());
        response.setComment(entry.getComment());
        response.setActedAt(entry.getActedAt());
        response.setActorRole(entry.getActorRole());
        if (entry.getActor() != null) {
            response.setActorId(entry.getActor().getUserId());
            response.setActorUsername(entry.getActor().getUsername());
            response.setActorDisplayName(entry.getActor().getFirstName() + " " + entry.getActor().getLastName());
        }
        return response;
    }

    private List<String> resolveAllowedActions(Reimbursement reimbursement, UserRole role) {
        List<String> actions = new ArrayList<>();
        ReimbursementStatus status = reimbursement.getStatus();
        if (status == null) {
            return actions;
        }
        if (role.actionableStatuses().contains(status)
                || (role.isAdmin() && (status.isReviewStage() || status.isVendorIntegrationStage()))) {
            if (status.isReviewStage()
                    || status == ReimbursementStatus.MANAGER_APPROVAL
                    || status == ReimbursementStatus.REQUIRES_SENIOR_APPROVAL) {
                actions.add("APPROVE");
                actions.add("DENY");
            }
            if ((status == ReimbursementStatus.FAILED_VENDOR_SYNC
                    || status == ReimbursementStatus.PENDING_VENDOR_CONFIRMATION)
                    && role.canProcessVendor()) {
                actions.add("RETRY_VENDOR_SYNC");
            }
            if (status == ReimbursementStatus.VENDOR_PROCESSING && role.canProcessVendor()) {
                actions.add("MARK_PAID");
            }
        }
        return actions;
    }

    private WorkflowTimelineResponse buildTimeline(Reimbursement reimbursement, List<ApprovalHistory> history) {
        WorkflowTimelineResponse timeline = new WorkflowTimelineResponse();
        List<WorkflowTimelineResponse.TimelineStage> stages = new ArrayList<>();

        boolean needsSenior = reimbursement.isRequiresSeniorReview();
        ReimbursementStatus current = reimbursement.getStatus();
        boolean denied = current == ReimbursementStatus.DENIED;

        stages.add(stage("SUBMITTED", "Submitted", ReimbursementStatus.SUBMITTED,
                resolveStageState(ReimbursementStatus.SUBMITTED, current, needsSenior, denied, history),
                false, findCompletion(history, ApprovalAction.SUBMITTED, ReimbursementStatus.MANAGER_REVIEW)));

        stages.add(stage("MANAGER_REVIEW", "Manager Review", ReimbursementStatus.MANAGER_REVIEW,
                resolveStageState(ReimbursementStatus.MANAGER_REVIEW, current, needsSenior, denied, history),
                false, findApprovalOf(history, ReimbursementStatus.MANAGER_REVIEW)));

        stages.add(stage("SENIOR_MANAGER_REVIEW", "Senior Manager Review", ReimbursementStatus.SENIOR_MANAGER_REVIEW,
                needsSenior
                        ? resolveStageState(ReimbursementStatus.SENIOR_MANAGER_REVIEW, current, needsSenior, denied, history)
                        : "skipped",
                !needsSenior,
                findApprovalOf(history, ReimbursementStatus.SENIOR_MANAGER_REVIEW)));

        stages.add(stage("FINANCE_REVIEW", "Finance Review", ReimbursementStatus.FINANCE_REVIEW,
                resolveStageState(ReimbursementStatus.FINANCE_REVIEW, current, needsSenior, denied, history),
                false, findApprovalOf(history, ReimbursementStatus.FINANCE_REVIEW)));

        stages.add(stage("VENDOR_SYNC", "Vendor ERP Sync", ReimbursementStatus.PENDING_VENDOR_CONFIRMATION,
                resolveVendorSyncStageState(current, denied),
                false, findVendorSyncCompletion(history)));

        stages.add(stage("VENDOR_PROCESSING", "Vendor Processing", ReimbursementStatus.VENDOR_PROCESSING,
                resolveStageState(ReimbursementStatus.VENDOR_PROCESSING, current, needsSenior, denied, history),
                false, findCompletion(history, ApprovalAction.VENDOR_MARKED_PAID, ReimbursementStatus.PAID)));

        stages.add(stage("PAID", "Paid", ReimbursementStatus.PAID,
                current == ReimbursementStatus.PAID || current == ReimbursementStatus.APPROVED
                        ? "completed"
                        : (denied ? "denied" : "upcoming"),
                false,
                current == ReimbursementStatus.PAID
                        ? findCompletion(history, ApprovalAction.VENDOR_MARKED_PAID, ReimbursementStatus.PAID)
                        : null));

        timeline.setStages(stages);
        return timeline;
    }

    private String resolveVendorSyncStageState(ReimbursementStatus current, boolean denied) {
        if (denied) {
            return "upcoming";
        }
        if (current == ReimbursementStatus.FAILED_VENDOR_SYNC) {
            return "denied";
        }
        if (current == ReimbursementStatus.PENDING_VENDOR_CONFIRMATION) {
            return "current";
        }
        if (current == ReimbursementStatus.VENDOR_PROCESSING
                || current == ReimbursementStatus.PAID
                || current == ReimbursementStatus.APPROVED) {
            return "completed";
        }
        return "upcoming";
    }

    private CompletionMeta findVendorSyncCompletion(List<ApprovalHistory> history) {
        return history.stream()
                .filter(h -> h.getAction() == ApprovalAction.VENDOR_SYNC_SUCCESS)
                .findFirst()
                .map(this::toMeta)
                .orElse(null);
    }

    private String resolveStageState(ReimbursementStatus stage, ReimbursementStatus current,
                                     boolean needsSenior, boolean denied, List<ApprovalHistory> history) {
        if (denied) {
            if (stage == current || wasDeniedAt(history, stage)) {
                return "denied";
            }
            if (isStageBefore(stage, findDeniedFrom(history))) {
                return "completed";
            }
            return "upcoming";
        }

        List<ReimbursementStatus> order = pipelineOrder(needsSenior);
        int stageIdx = order.indexOf(normalize(stage));
        int currentIdx = order.indexOf(normalize(current));
        if (stageIdx < 0) {
            return "upcoming";
        }
        if (current == ReimbursementStatus.PAID || current == ReimbursementStatus.APPROVED) {
            return "completed";
        }
        if (currentIdx < 0) {
            return "upcoming";
        }
        if (stageIdx < currentIdx) {
            return "completed";
        }
        if (stageIdx == currentIdx) {
            return "current";
        }
        return "upcoming";
    }

    private List<ReimbursementStatus> pipelineOrder(boolean needsSenior) {
        List<ReimbursementStatus> order = new ArrayList<>();
        order.add(ReimbursementStatus.SUBMITTED);
        order.add(ReimbursementStatus.MANAGER_REVIEW);
        if (needsSenior) {
            order.add(ReimbursementStatus.SENIOR_MANAGER_REVIEW);
        }
        order.add(ReimbursementStatus.FINANCE_REVIEW);
        order.add(ReimbursementStatus.PENDING_VENDOR_CONFIRMATION);
        order.add(ReimbursementStatus.VENDOR_PROCESSING);
        order.add(ReimbursementStatus.PAID);
        return order;
    }

    private ReimbursementStatus normalize(ReimbursementStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case MANAGER_APPROVAL -> ReimbursementStatus.MANAGER_REVIEW;
            case REQUIRES_SENIOR_APPROVAL -> ReimbursementStatus.SENIOR_MANAGER_REVIEW;
            case APPROVED -> ReimbursementStatus.PAID;
            case FAILED_VENDOR_SYNC -> ReimbursementStatus.PENDING_VENDOR_CONFIRMATION;
            default -> status;
        };
    }

    private boolean isStageBefore(ReimbursementStatus stage, ReimbursementStatus other) {
        if (other == null) {
            return false;
        }
        List<ReimbursementStatus> order = pipelineOrder(true);
        return order.indexOf(normalize(stage)) < order.indexOf(normalize(other));
    }

    private ReimbursementStatus findDeniedFrom(List<ApprovalHistory> history) {
        return history.stream()
                .filter(h -> h.getAction() == ApprovalAction.DENIED)
                .map(ApprovalHistory::getFromStatus)
                .findFirst()
                .orElse(null);
    }

    private boolean wasDeniedAt(List<ApprovalHistory> history, ReimbursementStatus stage) {
        return history.stream().anyMatch(h ->
                h.getAction() == ApprovalAction.DENIED && normalize(h.getFromStatus()) == normalize(stage));
    }

    private CompletionMeta findApprovalOf(List<ApprovalHistory> history, ReimbursementStatus fromStage) {
        return history.stream()
                .filter(h -> h.getAction() == ApprovalAction.APPROVED
                        && normalize(h.getFromStatus()) == normalize(fromStage))
                .findFirst()
                .map(this::toMeta)
                .orElse(null);
    }

    private CompletionMeta findCompletion(List<ApprovalHistory> history, ApprovalAction action,
                                          ReimbursementStatus toStatus) {
        return history.stream()
                .filter(h -> h.getAction() == action || h.getToStatus() == toStatus)
                .findFirst()
                .map(this::toMeta)
                .orElse(null);
    }

    private CompletionMeta toMeta(ApprovalHistory h) {
        String by = h.getActor() != null
                ? h.getActor().getFirstName() + " " + h.getActor().getLastName()
                : h.getActorRole();
        return new CompletionMeta(h.getActedAt(), by);
    }

    private WorkflowTimelineResponse.TimelineStage stage(String key, String label, ReimbursementStatus status,
                                                         String state, boolean skipped, CompletionMeta meta) {
        WorkflowTimelineResponse.TimelineStage s = new WorkflowTimelineResponse.TimelineStage();
        s.setKey(key);
        s.setLabel(label);
        s.setStatus(status);
        s.setState(state);
        s.setSkipped(skipped);
        if (meta != null && meta.at() != null) {
            s.setCompletedAt(new WorkflowTimelineResponse.InstantStamp(
                    meta.at().toString(), meta.at().toEpochMilli()));
            s.setCompletedBy(meta.by());
        }
        return s;
    }

    private record CompletionMeta(Instant at, String by) {
    }
}

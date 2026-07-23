package com.reimbursement.workflow;

import com.reimbursement.entity.Reimbursement;
import com.reimbursement.entity.User;
import com.reimbursement.entity.WorkflowConfig;
import com.reimbursement.enums.ApprovalAction;
import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.enums.UserRole;
import com.reimbursement.exception.BadRequestException;
import com.reimbursement.exception.ForbiddenException;
import org.springframework.stereotype.Component;

/**
 * Enterprise multi-stage approval workflow:
 * <pre>
 * Submitted → Manager Review → [Senior Manager Review] → Finance Review
 *   → Pending Vendor Confirmation → Vendor Processing → Paid
 *                              ↘ Failed Vendor Sync
 * </pre>
 */
@Component
public class ReimbursementWorkflow {

    public record TransitionResult(ReimbursementStatus from, ReimbursementStatus to, ApprovalAction action) {
    }

    public TransitionResult onSubmit(Reimbursement reimbursement) {
        reimbursement.setStatus(ReimbursementStatus.MANAGER_REVIEW);
        return new TransitionResult(ReimbursementStatus.SUBMITTED, ReimbursementStatus.MANAGER_REVIEW,
                ApprovalAction.SUBMITTED);
    }

    public TransitionResult approve(Reimbursement reimbursement, User actor, WorkflowConfig config) {
        UserRole role = UserRole.fromValue(actor.getRole());
        ReimbursementStatus current = normalize(reimbursement.getStatus());
        assertCanAct(role, current);

        ReimbursementStatus next = nextOnApprove(reimbursement, current, config);
        reimbursement.setStatus(next);
        return new TransitionResult(current, next, ApprovalAction.APPROVED);
    }

    public TransitionResult deny(Reimbursement reimbursement, User actor) {
        UserRole role = UserRole.fromValue(actor.getRole());
        ReimbursementStatus current = normalize(reimbursement.getStatus());
        if (!current.isReviewStage() && current != ReimbursementStatus.MANAGER_APPROVAL
                && current != ReimbursementStatus.REQUIRES_SENIOR_APPROVAL) {
            throw new BadRequestException("Only claims in a review stage can be denied. Current: " + current);
        }
        assertCanAct(role, current);
        reimbursement.setStatus(ReimbursementStatus.DENIED);
        return new TransitionResult(current, ReimbursementStatus.DENIED, ApprovalAction.DENIED);
    }

    public TransitionResult markPaid(Reimbursement reimbursement, User actor) {
        UserRole role = UserRole.fromValue(actor.getRole());
        ReimbursementStatus current = normalize(reimbursement.getStatus());
        if (current != ReimbursementStatus.VENDOR_PROCESSING) {
            throw new BadRequestException(
                    "Only vendor-confirmed claims can be marked paid. Current: " + current);
        }
        if (!role.canProcessVendor()) {
            throw new ForbiddenException("Role " + role.getValue() + " cannot mark vendor payments");
        }
        reimbursement.setStatus(ReimbursementStatus.PAID);
        return new TransitionResult(current, ReimbursementStatus.PAID, ApprovalAction.VENDOR_MARKED_PAID);
    }

    /** Moves a claim into the ERP pending state (before or during vendor call). */
    public TransitionResult beginVendorSync(Reimbursement reimbursement) {
        ReimbursementStatus from = normalize(reimbursement.getStatus());
        reimbursement.setStatus(ReimbursementStatus.PENDING_VENDOR_CONFIRMATION);
        return new TransitionResult(from, ReimbursementStatus.PENDING_VENDOR_CONFIRMATION,
                ApprovalAction.VENDOR_SYNC_PENDING);
    }

    public TransitionResult vendorSyncSucceeded(Reimbursement reimbursement) {
        ReimbursementStatus from = normalize(reimbursement.getStatus());
        reimbursement.setStatus(ReimbursementStatus.VENDOR_PROCESSING);
        return new TransitionResult(from, ReimbursementStatus.VENDOR_PROCESSING,
                ApprovalAction.VENDOR_SYNC_SUCCESS);
    }

    public TransitionResult vendorSyncFailed(Reimbursement reimbursement) {
        ReimbursementStatus from = normalize(reimbursement.getStatus());
        reimbursement.setStatus(ReimbursementStatus.FAILED_VENDOR_SYNC);
        return new TransitionResult(from, ReimbursementStatus.FAILED_VENDOR_SYNC,
                ApprovalAction.VENDOR_SYNC_FAILED);
    }

    private ReimbursementStatus nextOnApprove(Reimbursement reimbursement, ReimbursementStatus current,
                                              WorkflowConfig config) {
        return switch (current) {
            case MANAGER_REVIEW, MANAGER_APPROVAL -> {
                if (reimbursement.isRequiresSeniorReview() && config.isSeniorManagerStageEnabled()) {
                    yield ReimbursementStatus.SENIOR_MANAGER_REVIEW;
                }
                yield config.isFinanceStageEnabled()
                        ? ReimbursementStatus.FINANCE_REVIEW
                        : ReimbursementStatus.PENDING_VENDOR_CONFIRMATION;
            }
            case SENIOR_MANAGER_REVIEW, REQUIRES_SENIOR_APPROVAL -> config.isFinanceStageEnabled()
                    ? ReimbursementStatus.FINANCE_REVIEW
                    : ReimbursementStatus.PENDING_VENDOR_CONFIRMATION;
            case FINANCE_REVIEW -> ReimbursementStatus.PENDING_VENDOR_CONFIRMATION;
            default -> throw new BadRequestException(
                    "Cannot approve from status " + current + ". Expected a review stage.");
        };
    }

    private void assertCanAct(UserRole role, ReimbursementStatus status) {
        ReimbursementStatus normalized = normalize(status);
        if (role.isAdmin()) {
            return;
        }
        boolean allowed = switch (normalized) {
            case MANAGER_REVIEW -> role.canApproveManagerStage();
            case SENIOR_MANAGER_REVIEW -> role.canApproveSeniorStage();
            case FINANCE_REVIEW -> role.canApproveFinanceStage();
            default -> false;
        };
        if (!allowed) {
            throw new ForbiddenException(
                    "Role " + role.getValue() + " cannot act on status " + normalized);
        }
    }

    private ReimbursementStatus normalize(ReimbursementStatus status) {
        if (status == null) {
            throw new BadRequestException("Reimbursement has no status");
        }
        return switch (status) {
            case MANAGER_APPROVAL -> ReimbursementStatus.MANAGER_REVIEW;
            case REQUIRES_SENIOR_APPROVAL -> ReimbursementStatus.SENIOR_MANAGER_REVIEW;
            case APPROVED -> ReimbursementStatus.PAID;
            default -> status;
        };
    }
}

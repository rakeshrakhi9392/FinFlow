package com.reimbursement.workflow;

import com.reimbursement.entity.Reimbursement;
import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.exception.BadRequestException;
import org.springframework.stereotype.Component;

/**
 * Encapsulates reimbursement status transitions for the enterprise approval workflow.
 */
@Component
public class ReimbursementWorkflow {

    public void approve(Reimbursement reimbursement) {
        requireAwaitingApproval(reimbursement);
        applyTransition(reimbursement, ReimbursementStatus.APPROVED);
    }

    public void deny(Reimbursement reimbursement) {
        requireAwaitingApproval(reimbursement);
        applyTransition(reimbursement, ReimbursementStatus.DENIED);
    }

    public void applyTransition(Reimbursement reimbursement, ReimbursementStatus targetStatus) {
        reimbursement.setStatus(targetStatus);
    }

    private void requireAwaitingApproval(Reimbursement reimbursement) {
        if (reimbursement.getStatus() == null || !reimbursement.getStatus().isAwaitingApproval()) {
            throw new BadRequestException(
                    "Only reimbursements awaiting approval can be resolved. Current status: "
                            + reimbursement.getStatus());
        }
    }
}

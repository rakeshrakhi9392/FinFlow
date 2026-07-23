package com.reimbursement.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Optional comment when approving, denying, or marking paid.
 */
public class ApprovalDecisionRequest {

    @Size(max = 1000, message = "Comment must be at most 1000 characters")
    private String comment;

    public ApprovalDecisionRequest() {
    }

    public ApprovalDecisionRequest(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

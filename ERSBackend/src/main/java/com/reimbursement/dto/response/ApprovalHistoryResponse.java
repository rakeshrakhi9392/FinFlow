package com.reimbursement.dto.response;

import com.reimbursement.enums.ApprovalAction;
import com.reimbursement.enums.ReimbursementStatus;

import java.time.Instant;

public class ApprovalHistoryResponse {

    private Long id;
    private ApprovalAction action;
    private ReimbursementStatus fromStatus;
    private ReimbursementStatus toStatus;
    private String comment;
    private Instant actedAt;
    private Integer actorId;
    private String actorUsername;
    private String actorDisplayName;
    private String actorRole;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApprovalAction getAction() {
        return action;
    }

    public void setAction(ApprovalAction action) {
        this.action = action;
    }

    public ReimbursementStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(ReimbursementStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public ReimbursementStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(ReimbursementStatus toStatus) {
        this.toStatus = toStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getActedAt() {
        return actedAt;
    }

    public void setActedAt(Instant actedAt) {
        this.actedAt = actedAt;
    }

    public Integer getActorId() {
        return actorId;
    }

    public void setActorId(Integer actorId) {
        this.actorId = actorId;
    }

    public String getActorUsername() {
        return actorUsername;
    }

    public void setActorUsername(String actorUsername) {
        this.actorUsername = actorUsername;
    }

    public String getActorDisplayName() {
        return actorDisplayName;
    }

    public void setActorDisplayName(String actorDisplayName) {
        this.actorDisplayName = actorDisplayName;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }
}

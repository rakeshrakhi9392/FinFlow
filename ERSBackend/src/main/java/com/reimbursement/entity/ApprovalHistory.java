package com.reimbursement.entity;

import com.reimbursement.enums.ApprovalAction;
import com.reimbursement.enums.ReimbursementStatus;
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

import java.time.Instant;

@Entity
@Table(name = "approval_history")
public class ApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reimbursement_id", nullable = false)
    private Reimbursement reimbursement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalAction action;

    @Enumerated(EnumType.STRING)
    private ReimbursementStatus fromStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReimbursementStatus toStatus;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private Instant actedAt;

    @Column(length = 64)
    private String actorRole;

    public ApprovalHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reimbursement getReimbursement() {
        return reimbursement;
    }

    public void setReimbursement(Reimbursement reimbursement) {
        this.reimbursement = reimbursement;
    }

    public User getActor() {
        return actor;
    }

    public void setActor(User actor) {
        this.actor = actor;
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

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }
}

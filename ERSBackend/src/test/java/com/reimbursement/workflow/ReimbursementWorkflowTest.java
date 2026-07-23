package com.reimbursement.workflow;

import com.reimbursement.entity.Reimbursement;
import com.reimbursement.entity.User;
import com.reimbursement.entity.WorkflowConfig;
import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.exception.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReimbursementWorkflowTest {

    private ReimbursementWorkflow workflow;
    private WorkflowConfig config;
    private User manager;
    private User senior;
    private User finance;

    @BeforeEach
    void setUp() {
        workflow = new ReimbursementWorkflow();
        config = new WorkflowConfig();
        config.setSeniorManagerStageEnabled(true);
        config.setFinanceStageEnabled(true);
        manager = user("manager");
        senior = user("senior_manager");
        finance = user("finance");
    }

    @Test
    void submitMovesToManagerReview() {
        Reimbursement claim = new Reimbursement();
        claim.setStatus(ReimbursementStatus.SUBMITTED);
        var result = workflow.onSubmit(claim);
        assertEquals(ReimbursementStatus.MANAGER_REVIEW, claim.getStatus());
        assertEquals(ReimbursementStatus.MANAGER_REVIEW, result.to());
    }

    @Test
    void managerApprovalEscalatesWhenRequired() {
        Reimbursement claim = baseClaim(ReimbursementStatus.MANAGER_REVIEW);
        claim.setRequiresSeniorReview(true);
        var result = workflow.approve(claim, manager, config);
        assertEquals(ReimbursementStatus.SENIOR_MANAGER_REVIEW, result.to());
    }

    @Test
    void managerApprovalSkipsSeniorWhenNotRequired() {
        Reimbursement claim = baseClaim(ReimbursementStatus.MANAGER_REVIEW);
        claim.setRequiresSeniorReview(false);
        var result = workflow.approve(claim, manager, config);
        assertEquals(ReimbursementStatus.FINANCE_REVIEW, result.to());
    }

    @Test
    void financeApprovalMovesToVendor() {
        Reimbursement claim = baseClaim(ReimbursementStatus.FINANCE_REVIEW);
        var result = workflow.approve(claim, finance, config);
        assertEquals(ReimbursementStatus.VENDOR_PROCESSING, result.to());
    }

    @Test
    void markPaidFromVendor() {
        Reimbursement claim = baseClaim(ReimbursementStatus.VENDOR_PROCESSING);
        var result = workflow.markPaid(claim, finance);
        assertEquals(ReimbursementStatus.PAID, result.to());
    }

    @Test
    void managerCannotActOnFinanceStage() {
        Reimbursement claim = baseClaim(ReimbursementStatus.FINANCE_REVIEW);
        assertThrows(ForbiddenException.class, () -> workflow.approve(claim, manager, config));
    }

    @Test
    void seniorCannotActOnManagerStage() {
        Reimbursement claim = baseClaim(ReimbursementStatus.MANAGER_REVIEW);
        assertThrows(ForbiddenException.class, () -> workflow.approve(claim, senior, config));
    }

    private Reimbursement baseClaim(ReimbursementStatus status) {
        Reimbursement claim = new Reimbursement();
        claim.setAmount(100);
        claim.setStatus(status);
        return claim;
    }

    private User user(String role) {
        User u = new User();
        u.setUserId(1);
        u.setUsername(role);
        u.setRole(role);
        u.setFirstName("Test");
        u.setLastName("User");
        return u;
    }
}

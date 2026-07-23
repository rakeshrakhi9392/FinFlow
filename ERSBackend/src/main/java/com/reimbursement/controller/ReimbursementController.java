package com.reimbursement.controller;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.dto.request.ApprovalDecisionRequest;
import com.reimbursement.dto.request.CreateReimbursementRequest;
import com.reimbursement.dto.response.ReimbursementResponse;
import com.reimbursement.enums.UserRole;
import com.reimbursement.policy.ReimbursementPolicy;
import com.reimbursement.security.SessionAuthService;
import com.reimbursement.service.ReimbursementService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_REIMBURSEMENTS)
@CrossOrigin(origins = { AppConstants.CORS_ORIGIN_LOCAL }, allowedHeaders = "*", allowCredentials = "true")
public class ReimbursementController {

    private final ReimbursementService reimbursementService;
    private final SessionAuthService sessionAuthService;
    private final ReimbursementPolicy reimbursementPolicy;

    public ReimbursementController(ReimbursementService reimbursementService,
                                   SessionAuthService sessionAuthService,
                                   ReimbursementPolicy reimbursementPolicy) {
        this.reimbursementService = reimbursementService;
        this.sessionAuthService = sessionAuthService;
        this.reimbursementPolicy = reimbursementPolicy;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','SENIOR_MANAGER','FINANCE','ADMIN')")
    public ResponseEntity<List<ReimbursementResponse>> getAllReimbursements(HttpSession session) {
        UserRole role = sessionAuthService.requireRole(session);
        return ResponseEntity.ok(reimbursementService.findAll(role));
    }

    @GetMapping("/queue")
    @PreAuthorize("hasAnyRole('MANAGER','SENIOR_MANAGER','FINANCE','ADMIN')")
    public ResponseEntity<List<ReimbursementResponse>> getRoleQueue(HttpSession session) {
        UserRole role = sessionAuthService.requireRole(session);
        return ResponseEntity.ok(reimbursementService.findQueueForRole(role));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReimbursementResponse>> getReimbursementsByUserId(
            @PathVariable int userId, HttpSession session) {
        Integer requesterId = sessionAuthService.requireUserId(session);
        UserRole role = sessionAuthService.requireRole(session);
        reimbursementPolicy.requireOwnOrElevated(userId, requesterId, role.getValue());
        return ResponseEntity.ok(reimbursementService.findByUserId(userId, role));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReimbursementResponse> getById(@PathVariable int id, HttpSession session) {
        UserRole role = sessionAuthService.requireRole(session);
        Integer userId = sessionAuthService.requireUserId(session);
        ReimbursementResponse response = reimbursementService.findById(id, role);
        if (response.getSubmitterId() != null) {
            reimbursementPolicy.requireOwnOrElevated(response.getSubmitterId(), userId, role.getValue());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<ReimbursementResponse> createReimbursement(
            @Valid @RequestBody CreateReimbursementRequest request,
            HttpSession session) {
        Integer userId = sessionAuthService.requireUserId(session);
        return ResponseEntity.ok(reimbursementService.createReimbursement(request, userId));
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','SENIOR_MANAGER','FINANCE','ADMIN')")
    public ResponseEntity<ReimbursementResponse> approveReimbursement(
            @PathVariable int id,
            @RequestBody(required = false) @Valid ApprovalDecisionRequest decision,
            HttpSession session) {
        Integer actorId = sessionAuthService.requireUserId(session);
        return ResponseEntity.ok(reimbursementService.approveReimbursement(id, actorId,
                decision != null ? decision : new ApprovalDecisionRequest()));
    }

    @PostMapping("/deny/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','SENIOR_MANAGER','FINANCE','ADMIN')")
    public ResponseEntity<ReimbursementResponse> denyReimbursement(
            @PathVariable int id,
            @RequestBody(required = false) @Valid ApprovalDecisionRequest decision,
            HttpSession session) {
        Integer actorId = sessionAuthService.requireUserId(session);
        return ResponseEntity.ok(reimbursementService.denyReimbursement(id, actorId,
                decision != null ? decision : new ApprovalDecisionRequest()));
    }

    @PostMapping("/mark-paid/{id}")
    @PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
    public ResponseEntity<ReimbursementResponse> markPaid(
            @PathVariable int id,
            @RequestBody(required = false) @Valid ApprovalDecisionRequest decision,
            HttpSession session) {
        Integer actorId = sessionAuthService.requireUserId(session);
        return ResponseEntity.ok(reimbursementService.markPaid(id, actorId,
                decision != null ? decision : new ApprovalDecisionRequest()));
    }
}

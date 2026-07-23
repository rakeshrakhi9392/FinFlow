package com.reimbursement.controller;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.dto.request.CreateReimbursementRequest;
import com.reimbursement.dto.response.ReimbursementResponse;
import com.reimbursement.security.SessionAuthService;
import com.reimbursement.service.ReimbursementService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    public ReimbursementController(ReimbursementService reimbursementService,
                                   SessionAuthService sessionAuthService) {
        this.reimbursementService = reimbursementService;
        this.sessionAuthService = sessionAuthService;
    }

    @GetMapping
    public ResponseEntity<List<ReimbursementResponse>> getAllReimbursements() {
        return ResponseEntity.ok(reimbursementService.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReimbursementResponse>> getReimbursementsByUserId(@PathVariable int userId) {
        return ResponseEntity.ok(reimbursementService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ReimbursementResponse> createReimbursement(
            @Valid @RequestBody CreateReimbursementRequest request,
            HttpSession session) {
        Integer userId = sessionAuthService.requireUserId(session);
        return ResponseEntity.ok(reimbursementService.createReimbursement(request, userId));
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<ReimbursementResponse> approveReimbursement(@PathVariable int id) {
        return ResponseEntity.ok(reimbursementService.approveReimbursement(id));
    }

    @PostMapping("/deny/{id}")
    public ResponseEntity<ReimbursementResponse> denyReimbursement(@PathVariable int id) {
        return ResponseEntity.ok(reimbursementService.denyReimbursement(id));
    }
}

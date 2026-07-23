package com.reimbursement.controller;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.dto.request.UpdateWorkflowConfigRequest;
import com.reimbursement.dto.response.WorkflowConfigResponse;
import com.reimbursement.security.SessionAuthService;
import com.reimbursement.service.WorkflowConfigService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstants.API_WORKFLOW)
@CrossOrigin(origins = { AppConstants.CORS_ORIGIN_LOCAL }, allowedHeaders = "*", allowCredentials = "true")
public class WorkflowConfigController {

    private final WorkflowConfigService workflowConfigService;
    private final SessionAuthService sessionAuthService;

    public WorkflowConfigController(WorkflowConfigService workflowConfigService,
                                    SessionAuthService sessionAuthService) {
        this.workflowConfigService = workflowConfigService;
        this.sessionAuthService = sessionAuthService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SENIOR_MANAGER','FINANCE')")
    public ResponseEntity<WorkflowConfigResponse> getConfig(HttpSession session) {
        sessionAuthService.requireRole(session);
        return ResponseEntity.ok(workflowConfigService.getActiveConfigResponse());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkflowConfigResponse> updateConfig(
            @Valid @RequestBody UpdateWorkflowConfigRequest request,
            HttpSession session) {
        sessionAuthService.requireRole(session);
        return ResponseEntity.ok(workflowConfigService.updateConfig(request));
    }
}

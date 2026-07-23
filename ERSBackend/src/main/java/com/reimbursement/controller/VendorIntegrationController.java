package com.reimbursement.controller;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.dto.response.ReimbursementResponse;
import com.reimbursement.dto.response.VendorIntegrationResponse;
import com.reimbursement.security.SessionAuthService;
import com.reimbursement.service.FinanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Vendor / ERP integration dashboard and retry endpoints.
 *
 * <pre>
 * Controller → FinanceService → VendorIntegrationService → MockSapVendor
 * </pre>
 */
@RestController
@RequestMapping(AppConstants.API_VENDOR)
public class VendorIntegrationController {

    private final FinanceService financeService;
    private final SessionAuthService sessionAuthService;

    public VendorIntegrationController(FinanceService financeService, SessionAuthService sessionAuthService) {
        this.financeService = financeService;
        this.sessionAuthService = sessionAuthService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
    public ResponseEntity<List<VendorIntegrationResponse>> listIntegrations() {
        return ResponseEntity.ok(financeService.listVendorIntegrations());
    }

    @GetMapping("/{reimbursementId}")
    @PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
    public ResponseEntity<VendorIntegrationResponse> getIntegration(@PathVariable int reimbursementId) {
        return ResponseEntity.ok(financeService.getVendorIntegration(reimbursementId));
    }

    @PostMapping("/{reimbursementId}/retry")
    @PreAuthorize("hasAnyRole('FINANCE','ADMIN')")
    public ResponseEntity<ReimbursementResponse> retrySync(@PathVariable int reimbursementId, HttpSession session) {
        Integer actorId = sessionAuthService.requireUserId(session);
        return ResponseEntity.ok(financeService.retryVendorSync(reimbursementId, actorId));
    }
}

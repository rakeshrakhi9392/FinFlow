package com.reimbursement.service;

import com.reimbursement.dto.request.ApprovalDecisionRequest;
import com.reimbursement.dto.response.ReimbursementResponse;
import com.reimbursement.dto.response.VendorIntegrationResponse;
import com.reimbursement.entity.ApprovalHistory;
import com.reimbursement.entity.Reimbursement;
import com.reimbursement.entity.User;
import com.reimbursement.enums.ApprovalAction;
import com.reimbursement.enums.ReimbursementStatus;
import com.reimbursement.enums.UserRole;
import com.reimbursement.enums.VendorPaymentStatus;
import com.reimbursement.enums.VendorSyncStatus;
import com.reimbursement.exception.BadRequestException;
import com.reimbursement.exception.ForbiddenException;
import com.reimbursement.exception.ResourceNotFoundException;
import com.reimbursement.integration.vendor.VendorIntegrationException;
import com.reimbursement.integration.vendor.VendorIntegrationService;
import com.reimbursement.integration.vendor.VendorPostingRequest;
import com.reimbursement.integration.vendor.VendorPostingResult;
import com.reimbursement.mapper.ReimbursementMapper;
import com.reimbursement.policy.ReimbursementPolicy;
import com.reimbursement.repository.ReimbursementRepository;
import com.reimbursement.repository.UserRepository;
import com.reimbursement.workflow.ReimbursementWorkflow;
import com.reimbursement.workflow.ReimbursementWorkflow.TransitionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Finance orchestration layer.
 *
 * <pre>
 * Controller → FinanceService → VendorIntegrationService → MockSapVendor (or SAP / Oracle / FX)
 * </pre>
 *
 * <p>Keeps ERP concerns out of generic reimbursement approval code. Future vendor
 * implementations plug in via {@link VendorIntegrationService} without changing this class.</p>
 */
@Service
@Transactional(readOnly = true)
public class FinanceService {

    private static final Logger log = LoggerFactory.getLogger(FinanceService.class);

    private static final Set<ReimbursementStatus> RETRYABLE = EnumSet.of(
            ReimbursementStatus.FAILED_VENDOR_SYNC,
            ReimbursementStatus.PENDING_VENDOR_CONFIRMATION);

    private static final Set<ReimbursementStatus> DASHBOARD_STATUSES = EnumSet.of(
            ReimbursementStatus.PENDING_VENDOR_CONFIRMATION,
            ReimbursementStatus.FAILED_VENDOR_SYNC,
            ReimbursementStatus.VENDOR_PROCESSING,
            ReimbursementStatus.PAID);

    private final VendorIntegrationService vendorIntegrationService;
    private final ReimbursementRepository reimbursementRepository;
    private final UserRepository userRepository;
    private final ReimbursementMapper reimbursementMapper;
    private final ReimbursementWorkflow reimbursementWorkflow;
    private final ReimbursementPolicy reimbursementPolicy;
    private final BudgetService budgetService;

    public FinanceService(VendorIntegrationService vendorIntegrationService,
                          ReimbursementRepository reimbursementRepository,
                          UserRepository userRepository,
                          ReimbursementMapper reimbursementMapper,
                          ReimbursementWorkflow reimbursementWorkflow,
                          ReimbursementPolicy reimbursementPolicy,
                          BudgetService budgetService) {
        this.vendorIntegrationService = vendorIntegrationService;
        this.reimbursementRepository = reimbursementRepository;
        this.userRepository = userRepository;
        this.reimbursementMapper = reimbursementMapper;
        this.reimbursementWorkflow = reimbursementWorkflow;
        this.reimbursementPolicy = reimbursementPolicy;
        this.budgetService = budgetService;
    }

    /**
     * Called after an approval transition lands on {@link ReimbursementStatus#PENDING_VENDOR_CONFIRMATION}.
     * Applies budget spend (once) and posts to the configured vendor ERP.
     */
    @Transactional
    public ReimbursementResponse syncAfterFinanceCommitment(Reimbursement reimbursement, User actor) {
        applyBudgetIfNeeded(reimbursement);
        reimbursement.setVendorSyncStatus(VendorSyncStatus.PENDING_VENDOR_CONFIRMATION);
        reimbursement.setVendorSystem(vendorIntegrationService.vendorSystem());
        reimbursementRepository.save(reimbursement);

        recordHistory(reimbursement, actor,
                new TransitionResult(
                        reimbursement.getStatus(),
                        ReimbursementStatus.PENDING_VENDOR_CONFIRMATION,
                        ApprovalAction.VENDOR_SYNC_PENDING),
                "Awaiting vendor ERP confirmation (" + vendorIntegrationService.vendorSystem() + ")",
                actor.getRole());

        return executeVendorPost(reimbursement, actor, false);
    }

    @Transactional
    public ReimbursementResponse retryVendorSync(int reimbursementId, Integer actorId) {
        User actor = requireFinanceActor(actorId);
        Reimbursement reimbursement = findEntity(reimbursementId);

        if (!RETRYABLE.contains(reimbursement.getStatus())) {
            throw new BadRequestException(
                    "Retry is only allowed for FAILED_VENDOR_SYNC or PENDING_VENDOR_CONFIRMATION. Current: "
                            + reimbursement.getStatus());
        }

        TransitionResult pending = reimbursementWorkflow.beginVendorSync(reimbursement);
        reimbursement.setVendorSyncStatus(VendorSyncStatus.PENDING_VENDOR_CONFIRMATION);
        reimbursement.setVendorErrorCode(null);
        reimbursement.setVendorErrorMessage(null);
        recordHistory(reimbursement, actor,
                new TransitionResult(pending.from(), pending.to(), ApprovalAction.VENDOR_SYNC_RETRY),
                "Manual vendor sync retry", actor.getRole());

        return executeVendorPost(reimbursement, actor, true);
    }

    @Transactional
    public ReimbursementResponse markPaid(int id, Integer actorId, ApprovalDecisionRequest decision) {
        User actor = requireFinanceActor(actorId);
        Reimbursement reimbursement = findEntity(id);

        if (reimbursement.getVendorSyncStatus() != VendorSyncStatus.SYNCED
                || reimbursement.getAccountingDocument() == null
                || reimbursement.getAccountingDocument().isBlank()) {
            throw new BadRequestException(
                    "Cannot mark paid until vendor ERP sync succeeds with an accounting document");
        }

        TransitionResult result = reimbursementWorkflow.markPaid(reimbursement, actor);
        if (reimbursement.getVendorPaymentStatus() != VendorPaymentStatus.PAID) {
            reimbursement.setVendorPaymentStatus(VendorPaymentStatus.PAID);
        }
        recordHistory(reimbursement, actor, result, commentOf(decision), actor.getRole());
        return reimbursementMapper.toResponse(
                reimbursementRepository.save(reimbursement), UserRole.fromValue(actor.getRole()));
    }

    public List<VendorIntegrationResponse> listVendorIntegrations() {
        return reimbursementRepository.findByStatusIn(DASHBOARD_STATUSES).stream()
                .map(this::toVendorResponse)
                .toList();
    }

    public VendorIntegrationResponse getVendorIntegration(int reimbursementId) {
        return toVendorResponse(findEntity(reimbursementId));
    }

    private ReimbursementResponse executeVendorPost(Reimbursement reimbursement, User actor, boolean isRetry) {
        reimbursement.setVendorSyncAttempts(reimbursement.getVendorSyncAttempts() + 1);
        reimbursement.setLastVendorSyncAt(Instant.now());
        reimbursement.setVendorSystem(vendorIntegrationService.vendorSystem());

        try {
            VendorPostingResult result = vendorIntegrationService.postReimbursement(toPostingRequest(reimbursement));
            applySuccess(reimbursement, result);
            TransitionResult transition = reimbursementWorkflow.vendorSyncSucceeded(reimbursement);
            recordHistory(reimbursement, actor, transition,
                    "Vendor sync OK: " + result.accountingDocument()
                            + " / ref " + result.referenceNumber(),
                    actor.getRole());
            log.info("Vendor sync succeeded reimbursementId={} doc={} retry={}",
                    reimbursement.getReimbursementId(), result.accountingDocument(), isRetry);
        } catch (VendorIntegrationException ex) {
            applyFailure(reimbursement, ex);
            TransitionResult transition = reimbursementWorkflow.vendorSyncFailed(reimbursement);
            recordHistory(reimbursement, actor, transition,
                    "Vendor sync failed: " + ex.getErrorCode() + " — " + ex.getMessage(),
                    actor.getRole());
            log.warn("Vendor sync failed reimbursementId={} code={} retry={}",
                    reimbursement.getReimbursementId(), ex.getErrorCode(), isRetry);
        }

        return reimbursementMapper.toResponse(
                reimbursementRepository.save(reimbursement), UserRole.fromValue(actor.getRole()));
    }

    private void applySuccess(Reimbursement reimbursement, VendorPostingResult result) {
        reimbursement.setVendorSyncStatus(VendorSyncStatus.SYNCED);
        reimbursement.setAccountingDocument(result.accountingDocument());
        reimbursement.setVendorReferenceNumber(result.referenceNumber());
        reimbursement.setVendorPostingDate(result.postingDate());
        reimbursement.setVendorId(result.vendorId());
        reimbursement.setVendorPaymentStatus(result.paymentStatus());
        reimbursement.setVendorResponse(result.rawResponse());
        reimbursement.setVendorErrorCode(null);
        reimbursement.setVendorErrorMessage(null);
        reimbursement.setLastVendorSyncAt(result.respondedAt() != null ? result.respondedAt() : Instant.now());
    }

    private void applyFailure(Reimbursement reimbursement, VendorIntegrationException ex) {
        reimbursement.setVendorSyncStatus(VendorSyncStatus.FAILED_VENDOR_SYNC);
        reimbursement.setVendorErrorCode(ex.getErrorCode());
        reimbursement.setVendorErrorMessage(ex.getMessage());
        reimbursement.setVendorResponse(ex.getMessage());
        reimbursement.setVendorPaymentStatus(VendorPaymentStatus.NOT_POSTED);
        reimbursement.setLastVendorSyncAt(Instant.now());
    }

    private void applyBudgetIfNeeded(Reimbursement reimbursement) {
        if (reimbursement.getBudget() == null) {
            throw new BadRequestException("Reimbursement is not linked to a budget");
        }
        // Apply department spend once on first vendor commitment; retries never re-spend.
        if (reimbursement.getVendorSyncAttempts() == 0) {
            budgetService.applySpendAtomically(reimbursement.getBudget().getId(), reimbursement.getAmount());
        }
    }

    private VendorPostingRequest toPostingRequest(Reimbursement reimbursement) {
        String employeeKey = reimbursement.getUser() != null
                ? reimbursement.getUser().getUsername()
                : "unknown";
        String deptCode = reimbursement.getDepartment() != null
                ? reimbursement.getDepartment().getCode()
                : null;
        String categoryCode = reimbursement.getExpenseCategory() != null
                ? reimbursement.getExpenseCategory().getCode()
                : null;
        return new VendorPostingRequest(
                reimbursement.getReimbursementId(),
                reimbursement.getAmount(),
                "USD",
                reimbursement.getDescription(),
                employeeKey,
                deptCode,
                deptCode,
                categoryCode,
                reimbursement.getDateSubmitted());
    }

    private VendorIntegrationResponse toVendorResponse(Reimbursement r) {
        VendorIntegrationResponse response = new VendorIntegrationResponse();
        response.setReimbursementId(r.getReimbursementId());
        response.setAmount(r.getAmount());
        response.setDescription(r.getDescription());
        response.setStatus(r.getStatus());
        if (r.getUser() != null) {
            response.setSubmitterUsername(r.getUser().getUsername());
        }
        if (r.getDepartment() != null) {
            response.setDepartmentName(r.getDepartment().getName());
        }
        response.setIntegrationStatus(r.getVendorSyncStatus());
        response.setVendorSystem(r.getVendorSystem());
        response.setAccountingDocument(r.getAccountingDocument());
        response.setReferenceNumber(r.getVendorReferenceNumber());
        response.setPostingDate(r.getVendorPostingDate());
        response.setVendorId(r.getVendorId());
        response.setPaymentStatus(r.getVendorPaymentStatus());
        response.setLastSyncAt(r.getLastVendorSyncAt());
        response.setSyncAttempts(r.getVendorSyncAttempts());
        response.setVendorResponse(r.getVendorResponse());
        response.setErrorCode(r.getVendorErrorCode());
        response.setErrorMessage(r.getVendorErrorMessage());
        response.setRetryAllowed(RETRYABLE.contains(r.getStatus()));
        return response;
    }

    private User requireFinanceActor(Integer actorId) {
        reimbursementPolicy.requireAuthenticated(actorId);
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor user not found: " + actorId));
        UserRole role = UserRole.fromValue(actor.getRole());
        if (!role.canProcessVendor()) {
            throw new ForbiddenException("Only finance or admin may manage vendor integration");
        }
        return actor;
    }

    private Reimbursement findEntity(int id) {
        return reimbursementRepository.findByIdWithHistory(id)
                .orElseGet(() -> reimbursementRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Reimbursement request not found")));
    }

    private void recordHistory(Reimbursement reimbursement, User actor, TransitionResult result,
                               String comment, String actorRole) {
        ApprovalHistory entry = new ApprovalHistory();
        entry.setActor(actor);
        entry.setAction(result.action());
        entry.setFromStatus(result.from());
        entry.setToStatus(result.to());
        entry.setComment(comment);
        entry.setActedAt(Instant.now());
        entry.setActorRole(actorRole);
        reimbursement.addHistory(entry);
    }

    private String commentOf(ApprovalDecisionRequest decision) {
        return decision != null ? decision.getComment() : null;
    }
}

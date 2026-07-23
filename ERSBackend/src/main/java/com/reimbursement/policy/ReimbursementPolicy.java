package com.reimbursement.policy;

import com.reimbursement.entity.Reimbursement;
import com.reimbursement.enums.UserRole;
import com.reimbursement.exception.ForbiddenException;
import com.reimbursement.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

/**
 * Authorization / business-access rules for reimbursements (RBAC).
 */
@Component
public class ReimbursementPolicy {

    public void requireAuthenticated(Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Session expired or user not logged in.");
        }
    }

    public boolean canViewAll(String role) {
        return UserRole.fromValue(role).canViewAllReimbursements();
    }

    public boolean canResolve(String role) {
        UserRole userRole = UserRole.fromValue(role);
        return userRole.canApproveManagerStage()
                || userRole.canApproveSeniorStage()
                || userRole.canApproveFinanceStage()
                || userRole.canProcessVendor();
    }

    public boolean canView(Reimbursement reimbursement, int requestingUserId, String role) {
        if (canViewAll(role)) {
            return true;
        }
        return reimbursement.getUser() != null
                && reimbursement.getUser().getUserId() == requestingUserId;
    }

    public void requireCanViewAll(String role) {
        if (!canViewAll(role)) {
            throw new ForbiddenException("Insufficient privileges to view all reimbursements");
        }
    }

    public void requireCanConfigure(String role) {
        if (!UserRole.fromValue(role).canConfigureWorkflow()) {
            throw new ForbiddenException("Only admins can configure the workflow");
        }
    }

    public void requireOwnOrElevated(int resourceUserId, int requestingUserId, String role) {
        if (canViewAll(role)) {
            return;
        }
        if (resourceUserId != requestingUserId) {
            throw new ForbiddenException("You may only access your own reimbursements");
        }
    }
}

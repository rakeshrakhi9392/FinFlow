package com.reimbursement.policy;

import com.reimbursement.entity.Reimbursement;
import com.reimbursement.enums.UserRole;
import com.reimbursement.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

/**
 * Authorization / business-access rules for reimbursements.
 * Separated from workflow transitions and from HTTP concerns.
 */
@Component
public class ReimbursementPolicy {

    public void requireAuthenticated(Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Session expired or user not logged in.");
        }
    }

    public boolean canViewAll(String role) {
        return UserRole.fromValue(role).isManager();
    }

    public boolean canResolve(String role) {
        return UserRole.fromValue(role).isManager();
    }

    public boolean canView(Reimbursement reimbursement, int requestingUserId, String role) {
        if (canViewAll(role)) {
            return true;
        }
        return reimbursement.getUser() != null
                && reimbursement.getUser().getUserId() == requestingUserId;
    }
}

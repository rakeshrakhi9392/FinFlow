package com.reimbursement.security;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.exception.UnauthorizedException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

/**
 * Session-based auth helpers. Full Spring Security can replace this later
 * without changing controllers if they depend on this facade.
 */
@Component
public class SessionAuthService {

    public void establishSession(HttpSession session, int userId, String username, String role) {
        session.setAttribute(AppConstants.SESSION_USER_ID, userId);
        session.setAttribute(AppConstants.SESSION_USERNAME, username);
        session.setAttribute(AppConstants.SESSION_ROLE, role);
    }

    public void clearSession(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    public Integer requireUserId(HttpSession session) {
        Integer userId = getUserId(session);
        if (userId == null) {
            throw new UnauthorizedException("Session expired or user not logged in.");
        }
        return userId;
    }

    public Integer getUserId(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(AppConstants.SESSION_USER_ID);
        return value instanceof Integer ? (Integer) value : null;
    }

    public String getRole(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(AppConstants.SESSION_ROLE);
        return value instanceof String ? (String) value : null;
    }

    public String getUsername(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(AppConstants.SESSION_USERNAME);
        return value instanceof String ? (String) value : null;
    }
}

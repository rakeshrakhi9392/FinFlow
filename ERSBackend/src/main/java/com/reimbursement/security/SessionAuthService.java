package com.reimbursement.security;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.enums.UserRole;
import com.reimbursement.exception.UnauthorizedException;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bridges HTTP session attributes with Spring Security's SecurityContext.
 */
@Component
public class SessionAuthService {

    public void establishSession(HttpSession session, int userId, String username, String role) {
        session.setAttribute(AppConstants.SESSION_USER_ID, userId);
        session.setAttribute(AppConstants.SESSION_USERNAME, username);
        session.setAttribute(AppConstants.SESSION_ROLE, role);

        UserRole userRole = UserRole.fromValue(role);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + userRole.springRole()))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    public void clearSession(HttpSession session) {
        SecurityContextHolder.clearContext();
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

    public UserRole requireRole(HttpSession session) {
        String role = getRole(session);
        if (role == null) {
            throw new UnauthorizedException("Session expired or user not logged in.");
        }
        return UserRole.fromValue(role);
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

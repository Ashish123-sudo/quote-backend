package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * SecurityHelper provides centralized access to the current user's
 * organization and identity from request headers.
 *
 * Angular sends these headers on every API call:
 *   X-Org-Id:  the logged-in user's orgId
 *   X-User-Id: the logged-in user's userId
 *
 * TODO: Replace header-based approach with JWT token extraction
 * once JWT authentication is fully implemented.
 */
@Component
public class SecurityHelper {

    private static final String HEADER_ORG_ID  = "X-Org-Id";
    private static final String HEADER_USER_ID = "X-User-Id";

    // Fallback super-admin org ID used when no header is present
    // (e.g. during testing or Postman calls)
    private static final String FALLBACK_ORG_ID  = "10000000-0000-0000-0000-000000000001";
    private static final String FALLBACK_USER_ID = "00000000-0000-0000-0000-000000000001";

    /**
     * Get the current user's organization ID from the request header.
     */
    public UUID getCurrentOrgId() {
        String value = getHeader(HEADER_ORG_ID);
        if (value == null || value.isBlank()) {
            return UUID.fromString(FALLBACK_ORG_ID);
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return UUID.fromString(FALLBACK_ORG_ID);
        }
    }

    /**
     * Get the current user's ID from the request header.
     */
    public UUID getCurrentUserId() {
        String value = getHeader(HEADER_USER_ID);
        if (value == null || value.isBlank()) {
            return UUID.fromString(FALLBACK_USER_ID);
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return UUID.fromString(FALLBACK_USER_ID);
        }
    }

    /**
     * Get the current username from the request header.
     */
    public String getCurrentUsername() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return "system";
        String username = request.getHeader("X-Username");
        return (username != null && !username.isBlank()) ? username : "system";
    }

    // ── Private helper ───────────────────────────────────────────────

    private String getHeader(String headerName) {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return null;
        return request.getHeader(headerName);
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attrs.getRequest();
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
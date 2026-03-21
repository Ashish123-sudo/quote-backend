package com.example.demo.config;

import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * SecurityHelper provides centralized access to current user's organization and identity.
 *
 * TODO: This is currently using hardcoded values for testing.
 * In production, this should extract values from:
 * - JWT tokens
 * - Spring Security context
 * - Session attributes
 */
@Component
public class SecurityHelper {

    /**
     * Get the current user's organization ID.
     *
     * TODO: Replace with actual implementation that extracts from:
     * - JWT token claims
     * - SecurityContextHolder.getContext().getAuthentication()
     * - Custom UserPrincipal object
     *
     * For now, returns the sample organization ID from the database
     * (TechVision Inc - from sample data)
     */
    public UUID getCurrentOrgId() {
        // PRODUCTION CODE SHOULD LOOK LIKE:
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        // return principal.getOrgId();

        // FOR TESTING: Using sample org from database
        return UUID.fromString("10000000-0000-0000-0000-000000000001");
    }

    /**
     * Get the current user's ID.
     *
     * TODO: Replace with actual implementation that extracts from:
     * - JWT token claims
     * - SecurityContextHolder.getContext().getAuthentication()
     * - Custom UserPrincipal object
     *
     * For now, returns a placeholder UUID
     */
    public UUID getCurrentUserId() {
        // PRODUCTION CODE SHOULD LOOK LIKE:
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        // return principal.getUserId();

        // FOR TESTING: Using placeholder
        return UUID.fromString("00000000-0000-0000-0000-000000000001");
    }

    /**
     * Get the current user's username.
     *
     * TODO: Replace with actual implementation
     */
    public String getCurrentUsername() {
        // PRODUCTION CODE SHOULD LOOK LIKE:
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // return auth.getName();

        // FOR TESTING: Using placeholder
        return "system";
    }

    /**
     * Set organization context for testing purposes.
     * This allows you to switch between organizations during testing.
     *
     * WARNING: Remove this method in production!
     */
    private UUID testOrgId = UUID.fromString("10000000-0000-0000-0000-000000000001");

    public void setTestOrgId(UUID orgId) {
        this.testOrgId = orgId;
    }

    public UUID getTestOrgId() {
        return this.testOrgId;
    }
}
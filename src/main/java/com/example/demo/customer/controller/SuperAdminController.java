package com.example.demo.customer.controller;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.config.PlatformConstants;
import com.example.demo.customer.service.SuperAdminService;
import com.example.demo.organization.entity.Organization;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;
    private final SecurityHelper securityHelper;

    public SuperAdminController(SuperAdminService superAdminService,
                                SecurityHelper securityHelper) {
        this.superAdminService = superAdminService;
        this.securityHelper    = securityHelper;
    }

    // ── Guard: only platform org can call these endpoints ───────────

    private boolean isPlatformAdmin() {
        return PlatformConstants.isPlatformOrg(securityHelper.getCurrentOrgId());
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Access denied — platform admin only"));
    }

    // ── GET /api/admin/organizations ─────────────────────────────────

    @GetMapping("/organizations")
    public ResponseEntity<?> getAllOrganizations() {
        if (!isPlatformAdmin()) return forbidden();
        List<Organization> orgs = superAdminService.getAllOrganizations();
        return ResponseEntity.ok(orgs);
    }

    // ── POST /api/admin/organizations ────────────────────────────────

    @PostMapping("/organizations")
    public ResponseEntity<?> createOrganization(
            @RequestBody SuperAdminService.OnboardRequest request) {

        if (!isPlatformAdmin()) return forbidden();

        try {
            UUID createdBy = securityHelper.getCurrentUserId();
            Organization org = superAdminService.createOrganizationWithAdmin(request, createdBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(org);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create organization"));
        }
    }

    // ── PUT /api/admin/organizations/{id}/suspend ────────────────────

    @PutMapping("/organizations/{id}/suspend")
    public ResponseEntity<?> suspendOrganization(@PathVariable UUID id) {
        if (!isPlatformAdmin()) return forbidden();
        try {
            UUID updatedBy = securityHelper.getCurrentUserId();
            Organization org = superAdminService.suspendOrganization(id, updatedBy);
            return ResponseEntity.ok(org);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ── PUT /api/admin/organizations/{id}/reactivate ─────────────────

    @PutMapping("/organizations/{id}/reactivate")
    public ResponseEntity<?> reactivateOrganization(@PathVariable UUID id) {
        if (!isPlatformAdmin()) return forbidden();
        try {
            UUID updatedBy = securityHelper.getCurrentUserId();
            Organization org = superAdminService.reactivateOrganization(id, updatedBy);
            return ResponseEntity.ok(org);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
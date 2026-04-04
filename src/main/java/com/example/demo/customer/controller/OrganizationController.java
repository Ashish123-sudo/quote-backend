package com.example.demo.organization.controller;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.config.PlatformConstants;
import com.example.demo.organization.entity.Organization;
import com.example.demo.organization.entity.OrganizationContact;
import com.example.demo.organization.service.OrganizationContactService;
import com.example.demo.organization.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationContactService contactService;
    private final SecurityHelper securityHelper;

    public OrganizationController(OrganizationService organizationService,
                                  OrganizationContactService contactService,
                                  SecurityHelper securityHelper) {
        this.organizationService = organizationService;
        this.contactService      = contactService;
        this.securityHelper      = securityHelper;
    }

    // ── GET /api/organizations ───────────────────────────────────────
    // Platform admin → all orgs
    // Any other org  → only their own org

    @GetMapping
    public ResponseEntity<List<Organization>> getAllOrganizations(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {

        UUID currentOrgId = securityHelper.getCurrentOrgId();

        if (PlatformConstants.isPlatformOrg(currentOrgId)) {
            // Super admin sees all
            List<Organization> orgs = activeOnly
                    ? organizationService.getActiveOrganizations()
                    : organizationService.getAllOrganizations();
            return ResponseEntity.ok(orgs);
        } else {
            // Regular org sees only themselves
            return organizationService.getOrganizationById(currentOrgId)
                    .map(org -> ResponseEntity.ok(List.of(org)))
                    .orElse(ResponseEntity.ok(List.of()));
        }
    }

    @GetMapping("/{orgId}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable UUID orgId) {
        UUID currentOrgId = securityHelper.getCurrentOrgId();

        // Only allow access to own org unless platform admin
        if (!PlatformConstants.isPlatformOrg(currentOrgId) && !currentOrgId.equals(orgId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return organizationService.getOrganizationById(orgId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{orgCode}")
    public ResponseEntity<Organization> getOrganizationByCode(@PathVariable String orgCode) {
        return organizationService.getOrganizationByCode(orgCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createOrganization(@RequestBody Organization organization) {
        // Only platform admin can create orgs directly
        // (use /api/admin/organizations for onboarding with admin user)
        if (!PlatformConstants.isPlatformOrg(securityHelper.getCurrentOrgId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only platform admin can create organizations");
        }
        try {
            Organization created = organizationService.createOrganization(organization);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orgId}")
    public ResponseEntity<?> updateOrganization(@PathVariable UUID orgId,
                                                @RequestBody Organization organization) {
        UUID currentOrgId = securityHelper.getCurrentOrgId();

        // Org can only update themselves, platform admin can update any
        if (!PlatformConstants.isPlatformOrg(currentOrgId) && !currentOrgId.equals(orgId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Organization updated = organizationService.updateOrganization(orgId, organization);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{orgId}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID orgId) {
        if (!PlatformConstants.isPlatformOrg(securityHelper.getCurrentOrgId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Block deletion of platform org itself
        if (PlatformConstants.isPlatformOrg(orgId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            organizationService.deleteOrganization(orgId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{orgId}/deactivate")
    public ResponseEntity<Organization> deactivateOrganization(@PathVariable UUID orgId) {
        if (!PlatformConstants.isPlatformOrg(securityHelper.getCurrentOrgId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Organization org = organizationService.deactivateOrganization(orgId);
            return ResponseEntity.ok(org);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── Organization Contact endpoints (unchanged) ───────────────────

    @GetMapping("/{orgId}/contacts")
    public ResponseEntity<List<OrganizationContact>> getContactsByOrg(
            @PathVariable UUID orgId,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        List<OrganizationContact> contacts = activeOnly
                ? contactService.getActiveContactsByOrgId(orgId)
                : contactService.getContactsByOrgId(orgId);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{orgId}/contacts/primary")
    public ResponseEntity<OrganizationContact> getPrimaryContact(@PathVariable UUID orgId) {
        return contactService.getPrimaryContact(orgId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{orgId}/contacts/{contactId}")
    public ResponseEntity<OrganizationContact> getContactById(@PathVariable UUID orgId,
                                                              @PathVariable UUID contactId) {
        return contactService.getContactById(contactId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{orgId}/contacts")
    public ResponseEntity<?> createContact(@PathVariable UUID orgId,
                                           @RequestBody OrganizationContact contact) {
        try {
            OrganizationContact created = contactService.createContact(orgId, contact);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orgId}/contacts/{contactId}")
    public ResponseEntity<?> updateContact(@PathVariable UUID orgId,
                                           @PathVariable UUID contactId,
                                           @RequestBody OrganizationContact contact) {
        try {
            OrganizationContact updated = contactService.updateContact(contactId, contact);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{orgId}/contacts/{contactId}")
    public ResponseEntity<Void> deleteContact(@PathVariable UUID orgId,
                                              @PathVariable UUID contactId) {
        try {
            contactService.deleteContact(contactId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{orgId}/contacts/{contactId}/deactivate")
    public ResponseEntity<OrganizationContact> deactivateContact(@PathVariable UUID orgId,
                                                                 @PathVariable UUID contactId) {
        try {
            OrganizationContact contact = contactService.deactivateContact(contactId);
            return ResponseEntity.ok(contact);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
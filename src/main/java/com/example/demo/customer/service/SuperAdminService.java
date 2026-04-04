package com.example.demo.customer.service;

import com.example.demo.customer.config.PlatformConstants;
import com.example.demo.customer.entity.AppRole;
import com.example.demo.customer.entity.AppUser;
import com.example.demo.customer.repository.AppRoleRepository;
import com.example.demo.customer.repository.AppUserRepository;
import com.example.demo.organization.entity.Organization;
import com.example.demo.organization.repository.OrganizationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SuperAdminService {

    private final OrganizationRepository organizationRepository;
    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminService(OrganizationRepository organizationRepository,
                             AppUserRepository appUserRepository,
                             AppRoleRepository appRoleRepository,
                             PasswordEncoder passwordEncoder) {
        this.organizationRepository = organizationRepository;
        this.appUserRepository      = appUserRepository;
        this.appRoleRepository      = appRoleRepository;
        this.passwordEncoder        = passwordEncoder;
    }

    // ── Seed roles if org has none ───────────────────────────────────

    @Transactional
    public void seedRolesIfMissing(UUID orgId, UUID createdBy) {
        if (appRoleRepository.findByOrgId(orgId).isEmpty()) {
            List<String> roleNames = List.of("Administrator", "Quote Creator", "Quote Approver");
            for (String roleName : roleNames) {
                AppRole role = new AppRole();
                role.setOrgId(orgId);
                role.setRoleName(roleName);
                role.setCreatedBy(createdBy);
                role.setUpdatedBy(createdBy);
                appRoleRepository.save(role);
            }
        }
    }

    // ── List all orgs ────────────────────────────────────────────────

    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll()
                .stream()
                .filter(o -> !PlatformConstants.isPlatformOrg(o.getOrgId()))
                .collect(java.util.stream.Collectors.toList());
    }

    // ── Create org + first admin user in one transaction ────────────

    @Transactional
    public Organization createOrganizationWithAdmin(OnboardRequest request, UUID createdBy) {

        if (organizationRepository.existsByOrgCode(request.orgCode())) {
            throw new IllegalArgumentException("Org code already exists: " + request.orgCode());
        }

        // 1. Create the org
        Organization org = new Organization();
        org.setOrgName(request.orgName());
        org.setOrgCode(request.orgCode());
        org.setEmail(request.orgEmail());
        org.setPhone(request.orgPhone());
        org.setIsActive(true);
        org.setCreatedBy(createdBy);
        org.setUpdatedBy(createdBy);
        Organization savedOrg = organizationRepository.save(org);

        // 2. Seed all 3 roles for the new org
        seedRolesIfMissing(savedOrg.getOrgId(), createdBy);

        // 3. Create the first admin user for the org
        AppRole adminRole = appRoleRepository
                .findByRoleNameAndOrgId("Administrator", savedOrg.getOrgId())
                .orElseThrow(() -> new RuntimeException("Administrator role not found after seeding"));

        AppUser adminUser = new AppUser();
        adminUser.setOrgId(savedOrg.getOrgId());
        adminUser.setUsername(request.adminUsername());
        adminUser.setPassword(passwordEncoder.encode(request.adminPassword()));
        adminUser.setFullName(request.adminFullName());
        adminUser.setEmail(request.adminEmail());
        adminUser.setIsActive(true);
        adminUser.setAppRole(adminRole);
        adminUser.setCreatedBy(createdBy);
        adminUser.setUpdatedBy(createdBy);
        appUserRepository.save(adminUser);

        return savedOrg;
    }

    // ── Suspend / reactivate org ─────────────────────────────────────

    @Transactional
    public Organization suspendOrganization(UUID orgId, UUID updatedBy) {
        if (PlatformConstants.isPlatformOrg(orgId)) {
            throw new IllegalArgumentException("Cannot suspend the platform org");
        }
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found: " + orgId));
        org.setIsActive(false);
        org.setUpdatedBy(updatedBy);
        return organizationRepository.save(org);
    }

    @Transactional
    public Organization reactivateOrganization(UUID orgId, UUID updatedBy) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found: " + orgId));
        org.setIsActive(true);
        org.setUpdatedBy(updatedBy);
        return organizationRepository.save(org);
    }

    // ── Request record ───────────────────────────────────────────────

    public record OnboardRequest(
            String orgName,
            String orgCode,
            String orgEmail,
            String orgPhone,
            String adminUsername,
            String adminPassword,
            String adminFullName,
            String adminEmail
    ) {}
}
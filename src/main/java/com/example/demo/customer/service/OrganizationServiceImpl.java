package com.example.demo.organization.service;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.config.PlatformConstants;
import com.example.demo.organization.entity.Organization;
import com.example.demo.organization.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final SecurityHelper securityHelper;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository,
                                   SecurityHelper securityHelper) {
        this.organizationRepository = organizationRepository;
        this.securityHelper = securityHelper;
    }

    @Override
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll()
                .stream()
                .filter(o -> !PlatformConstants.isPlatformOrg(o.getOrgId()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Organization> getActiveOrganizations() {
        return organizationRepository.findByIsActiveTrue();
    }

    @Override
    public Optional<Organization> getOrganizationById(UUID orgId) {
        return organizationRepository.findById(orgId);
    }

    @Override
    public Optional<Organization> getOrganizationByCode(String orgCode) {
        return organizationRepository.findByOrgCode(orgCode);
    }

    @Override
    public Organization createOrganization(Organization organization) {

        // Auto-generate orgCode if not provided — MUST be first
        if (organization.getOrgCode() == null || organization.getOrgCode().isBlank()) {
            String cleaned = organization.getOrgName()
                    .toUpperCase()
                    .replaceAll("[^A-Z0-9]", "");
            String code = cleaned.substring(0, Math.min(8, cleaned.length()));
            organization.setOrgCode(code + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        }

        // Validate org code uniqueness — AFTER auto-generate
        if (organizationRepository.existsByOrgCode(organization.getOrgCode())) {
            throw new IllegalArgumentException(
                    "Organization code already exists: " + organization.getOrgCode());
        }

        // Tag audit fields
        UUID currentUserId = securityHelper.getCurrentUserId();
        organization.setCreatedBy(currentUserId);
        organization.setUpdatedBy(currentUserId);

        // Default active
        if (organization.getIsActive() == null) {
            organization.setIsActive(true);
        }

        return organizationRepository.save(organization);
    }

    @Override
    public Organization updateOrganization(UUID orgId, Organization updated) {
        Organization existing = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found: " + orgId));

        // Preserve orgCode if not provided in update
        if (updated.getOrgCode() == null || updated.getOrgCode().isBlank()) {
            updated.setOrgCode(existing.getOrgCode());
        }

        // Check org code uniqueness only if it changed
        if (!existing.getOrgCode().equals(updated.getOrgCode()) &&
                organizationRepository.existsByOrgCode(updated.getOrgCode())) {
            throw new IllegalArgumentException(
                    "Organization code already exists: " + updated.getOrgCode());
        }

        existing.setOrgName(updated.getOrgName());
        existing.setOrgCode(updated.getOrgCode());
        existing.setAddress1(updated.getAddress1());
        existing.setAddress2(updated.getAddress2());
        existing.setCity(updated.getCity());
        existing.setStateProvince(updated.getStateProvince());
        existing.setCountry(updated.getCountry());
        existing.setPostalCode(updated.getPostalCode());
        existing.setPhone(updated.getPhone());
        existing.setFax(updated.getFax());
        existing.setEmail(updated.getEmail());
        existing.setWebsite(updated.getWebsite());
        existing.setIndustry(updated.getIndustry());
        existing.setCompanySize(updated.getCompanySize());
        existing.setTaxId(updated.getTaxId());
        existing.setSubscriptionTier(updated.getSubscriptionTier());
        existing.setSubscriptionStartDate(updated.getSubscriptionStartDate());
        existing.setSubscriptionEndDate(updated.getSubscriptionEndDate());
        existing.setIsActive(updated.getIsActive());
        existing.setNotes(updated.getNotes());

        // Tag audit
        existing.setUpdatedBy(securityHelper.getCurrentUserId());

        return organizationRepository.save(existing);
    }

    @Override
    public void deleteOrganization(UUID orgId) {
        if (!organizationRepository.existsById(orgId)) {
            throw new RuntimeException("Organization not found: " + orgId);
        }
        organizationRepository.deleteById(orgId);
    }

    @Override
    public Organization deactivateOrganization(UUID orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found: " + orgId));
        org.setIsActive(false);
        org.setUpdatedBy(securityHelper.getCurrentUserId());
        return organizationRepository.save(org);
    }
}
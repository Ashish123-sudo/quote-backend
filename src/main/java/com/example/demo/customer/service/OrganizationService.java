package com.example.demo.organization.service;

import com.example.demo.organization.entity.Organization;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationService {

    List<Organization> getAllOrganizations();

    List<Organization> getActiveOrganizations();

    Optional<Organization> getOrganizationById(UUID orgId);

    Optional<Organization> getOrganizationByCode(String orgCode);

    Organization createOrganization(Organization organization);

    Organization updateOrganization(UUID orgId, Organization organization);

    void deleteOrganization(UUID orgId);

    Organization deactivateOrganization(UUID orgId);
}
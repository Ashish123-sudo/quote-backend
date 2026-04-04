package com.example.demo.organization.service;

import com.example.demo.organization.entity.OrganizationContact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationContactService {

    List<OrganizationContact> getContactsByOrgId(UUID orgId);

    List<OrganizationContact> getActiveContactsByOrgId(UUID orgId);

    Optional<OrganizationContact> getContactById(UUID contactId);

    Optional<OrganizationContact> getPrimaryContact(UUID orgId);

    OrganizationContact createContact(UUID orgId, OrganizationContact contact);

    OrganizationContact updateContact(UUID contactId, OrganizationContact contact);

    void deleteContact(UUID contactId);

    OrganizationContact deactivateContact(UUID contactId);
}
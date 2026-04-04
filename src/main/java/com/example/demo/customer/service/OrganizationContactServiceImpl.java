package com.example.demo.organization.service;

import com.example.demo.config.SecurityHelper;
import com.example.demo.organization.entity.Organization;
import com.example.demo.organization.entity.OrganizationContact;
import com.example.demo.organization.repository.OrganizationContactRepository;
import com.example.demo.organization.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationContactServiceImpl implements OrganizationContactService {

    private final OrganizationContactRepository contactRepository;
    private final OrganizationRepository organizationRepository;
    private final SecurityHelper securityHelper;

    public OrganizationContactServiceImpl(OrganizationContactRepository contactRepository,
                                          OrganizationRepository organizationRepository,
                                          SecurityHelper securityHelper) {
        this.contactRepository = contactRepository;
        this.organizationRepository = organizationRepository;
        this.securityHelper = securityHelper;
    }

    @Override
    public List<OrganizationContact> getContactsByOrgId(UUID orgId) {
        return contactRepository.findByOrganization_OrgId(orgId);
    }

    @Override
    public List<OrganizationContact> getActiveContactsByOrgId(UUID orgId) {
        return contactRepository.findByOrganization_OrgIdAndIsActiveTrue(orgId);
    }

    @Override
    public Optional<OrganizationContact> getContactById(UUID contactId) {
        return contactRepository.findById(contactId);
    }

    @Override
    public Optional<OrganizationContact> getPrimaryContact(UUID orgId) {
        return contactRepository.findByOrganization_OrgIdAndIsPrimaryTrue(orgId);
    }

    @Override
    public OrganizationContact createContact(UUID orgId, OrganizationContact contact) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found: " + orgId));

        // If this contact is being set as primary, clear the existing primary
        if (Boolean.TRUE.equals(contact.getIsPrimary())) {
            clearExistingPrimary(orgId);
        }

        // Link to organization
        contact.setOrganization(org);

        // Tag audit fields
        UUID currentUserId = securityHelper.getCurrentUserId();
        contact.setCreatedBy(currentUserId);
        contact.setUpdatedBy(currentUserId);

        // Default flags
        if (contact.getIsActive() == null) {
            contact.setIsActive(true);
        }
        if (contact.getIsPrimary() == null) {
            contact.setIsPrimary(false);
        }

        // Auto-populate contactName from first + last if not provided
        if ((contact.getContactName() == null || contact.getContactName().isBlank())
                && contact.getFirstName() != null) {
            String fullName = contact.getFirstName()
                    + (contact.getLastName() != null ? " " + contact.getLastName() : "");
            contact.setContactName(fullName.trim());
        }

        return contactRepository.save(contact);
    }

    @Override
    public OrganizationContact updateContact(UUID contactId, OrganizationContact updated) {
        OrganizationContact existing = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found: " + contactId));

        UUID orgId = existing.getOrganization().getOrgId();

        // Handle primary flag change
        if (Boolean.TRUE.equals(updated.getIsPrimary())
                && !Boolean.TRUE.equals(existing.getIsPrimary())) {
            clearExistingPrimary(orgId);
        }

        existing.setContactName(updated.getContactName());
        existing.setContactTitle(updated.getContactTitle());
        existing.setContactEmail(updated.getContactEmail());
        existing.setContactPhone(updated.getContactPhone());
        existing.setContactMobile(updated.getContactMobile());
        existing.setSalutation(updated.getSalutation());
        existing.setFirstName(updated.getFirstName());
        existing.setMiddleName(updated.getMiddleName());
        existing.setLastName(updated.getLastName());
        existing.setDesignation(updated.getDesignation());
        existing.setDepartment(updated.getDepartment());
        existing.setRole(updated.getRole());
        existing.setOfficeDeskNumber(updated.getOfficeDeskNumber());
        existing.setOfficeExtension(updated.getOfficeExtension());
        existing.setAlternateMobile(updated.getAlternateMobile());
        existing.setFaxNumber(updated.getFaxNumber());
        existing.setLinkedinProfile(updated.getLinkedinProfile());
        existing.setIsPrimary(updated.getIsPrimary());
        existing.setIsActive(updated.getIsActive());

        // Auto-populate contactName from first + last if not provided
        if ((updated.getContactName() == null || updated.getContactName().isBlank())
                && updated.getFirstName() != null) {
            String fullName = updated.getFirstName()
                    + (updated.getLastName() != null ? " " + updated.getLastName() : "");
            existing.setContactName(fullName.trim());
        }

        // Tag audit
        existing.setUpdatedBy(securityHelper.getCurrentUserId());

        return contactRepository.save(existing);
    }

    @Override
    public void deleteContact(UUID contactId) {
        if (!contactRepository.existsById(contactId)) {
            throw new RuntimeException("Contact not found: " + contactId);
        }
        contactRepository.deleteById(contactId);
    }

    @Override
    public OrganizationContact deactivateContact(UUID contactId) {
        OrganizationContact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found: " + contactId));
        contact.setIsActive(false);
        contact.setUpdatedBy(securityHelper.getCurrentUserId());
        return contactRepository.save(contact);
    }

    private void clearExistingPrimary(UUID orgId) {
        contactRepository.findByOrganization_OrgIdAndIsPrimaryTrue(orgId)
                .ifPresent(existing -> {
                    existing.setIsPrimary(false);
                    contactRepository.save(existing);
                });
    }
}
package com.example.demo.customer.repository;

import com.example.demo.customer.entity.OrganizationContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationContactRepository extends JpaRepository<OrganizationContact, UUID> {

    // Find by organization
    List<OrganizationContact> findByOrganization_OrgId(UUID orgId);

    // Find active contacts by organization
    List<OrganizationContact> findByOrganization_OrgIdAndIsActiveTrue(UUID orgId);

    // Find primary contact for an organization
    Optional<OrganizationContact> findByOrganization_OrgIdAndIsPrimaryTrue(UUID orgId);

    // Find by email
    Optional<OrganizationContact> findByContactEmail(String contactEmail);
    List<OrganizationContact> findByContactEmailContainingIgnoreCase(String email);

    // Find by name
    List<OrganizationContact> findByContactNameContainingIgnoreCaseAndOrganization_OrgId(
            String name, UUID orgId
    );

    // Check existence
    boolean existsByContactEmailAndOrganization_OrgId(String contactEmail, UUID orgId);

    // Count contacts
    long countByOrganization_OrgId(UUID orgId);
    long countByOrganization_OrgIdAndIsActiveTrue(UUID orgId);
}
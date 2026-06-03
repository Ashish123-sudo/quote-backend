package com.example.demo.organization.repository;

import com.example.demo.organization.entity.OrganizationContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationContactRepository extends JpaRepository<OrganizationContact, UUID> {

    List<OrganizationContact> findByOrganization_OrgId(UUID orgId);

    List<OrganizationContact> findByOrganization_OrgIdAndIsActiveTrue(UUID orgId);

    Optional<OrganizationContact> findByOrganization_OrgIdAndIsPrimaryTrue(UUID orgId);

    boolean existsByOrganization_OrgIdAndIsPrimaryTrue(UUID orgId);
}
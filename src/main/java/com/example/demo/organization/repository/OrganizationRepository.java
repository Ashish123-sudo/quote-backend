package com.example.demo.organization.repository;

import com.example.demo.organization.entity.Organization;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    List<Organization> findByIsActiveTrue();
    Optional<Organization> findByOrgCode(String orgCode);
    boolean existsByOrgCode(String orgCode);
    List<Organization> findBySubscriptionTier(String subscriptionTier);
    List<Organization> findByIndustry(String industry);
    Optional<Organization> findByOrgIdAndOrgCode(UUID orgId, String orgCode);
    boolean existsByOrgCodeAndOrgIdNot(String orgCode, UUID orgId);

    // ✅ EntityGraph only — no @Query or @Param needed
    @EntityGraph(attributePaths = {"contacts"})
    Optional<Organization> findByOrgId(UUID orgId);      // loads org + contacts eagerly

    @EntityGraph(attributePaths = {"contacts"})
    List<Organization> findAll();                         // loads all orgs + contacts
}
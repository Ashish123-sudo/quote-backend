package com.example.demo.customer.repository;

import com.example.demo.customer.entity.TcTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TcTemplateRepository extends JpaRepository<TcTemplate, UUID> {

    // Find by ID with org_id filtering
    Optional<TcTemplate> findByTemplateIdAndOrgId(UUID templateId, UUID orgId);

    // Find by template name
    Optional<TcTemplate> findByTemplateNameAndOrgId(String templateName, UUID orgId);
    List<TcTemplate> findByTemplateNameContainingIgnoreCaseAndOrgId(String templateName, UUID orgId);

    // Find all templates for an organization
    List<TcTemplate> findByOrgId(UUID orgId);

    // Find active templates only
    List<TcTemplate> findByOrgIdAndIsActiveTrue(UUID orgId);

    // Check existence
    boolean existsByTemplateNameAndOrgId(String templateName, UUID orgId);

    // Count templates
    long countByOrgId(UUID orgId);
    long countByOrgIdAndIsActiveTrue(UUID orgId);

    // Custom query to find templates with specific term
    @Query("SELECT DISTINCT t FROM TcTemplate t " +
            "JOIN t.terms term " +
            "WHERE t.orgId = :orgId AND term.termId = :termId")
    List<TcTemplate> findTemplatesContainingTerm(
            @Param("orgId") UUID orgId,
            @Param("termId") UUID termId
    );
}
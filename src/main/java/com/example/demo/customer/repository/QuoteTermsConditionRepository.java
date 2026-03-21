package com.example.demo.quote.repository;

import com.example.demo.quote.entity.QuoteTermsCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteTermsConditionRepository extends JpaRepository<QuoteTermsCondition, UUID> {

    // Find by ID with org_id filtering
    Optional<QuoteTermsCondition> findByIdAndOrgId(UUID id, UUID orgId);

    // Find by quote
    List<QuoteTermsCondition> findByQuoteHeader_QuoteIdAndOrgId(UUID quoteId, UUID orgId);
    List<QuoteTermsCondition> findByQuoteRefAndOrgId(String quoteRef, UUID orgId);

    // Find by quote and ordered
    List<QuoteTermsCondition> findByQuoteRefAndOrgIdOrderByGroupOrderAscTermOrderAsc(
            String quoteRef, UUID orgId
    );

    // Find by group
    List<QuoteTermsCondition> findByQuoteRefAndGroupNameAndOrgId(
            String quoteRef, String groupName, UUID orgId
    );

    // Find all for organization
    List<QuoteTermsCondition> findByOrgId(UUID orgId);

    // Delete by quote
    void deleteByQuoteHeader_QuoteIdAndOrgId(UUID quoteId, UUID orgId);
    void deleteByQuoteRefAndOrgId(String quoteRef, UUID orgId);

    // Count terms
    long countByQuoteRefAndOrgId(String quoteRef, UUID orgId);
    long countByOrgId(UUID orgId);
}
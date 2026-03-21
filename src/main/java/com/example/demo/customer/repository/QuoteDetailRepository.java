package com.example.demo.quote.repository;

import com.example.demo.quote.entity.QuoteDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteDetailRepository extends JpaRepository<QuoteDetail, UUID> {

    // Find by ID with org_id filtering
    Optional<QuoteDetail> findBySlNoAndOrgId(UUID slNo, UUID orgId);

    // Find by quote
    List<QuoteDetail> findByQuoteHeader_QuoteIdAndOrgId(UUID quoteId, UUID orgId);
    List<QuoteDetail> findByQuoteRefAndOrgId(String quoteRef, UUID orgId);

    // Find all for organization
    List<QuoteDetail> findByOrgId(UUID orgId);

    // Find by item description
    List<QuoteDetail> findByItemDescContainingIgnoreCaseAndOrgId(String itemDesc, UUID orgId);

    // Delete by quote
    void deleteByQuoteHeader_QuoteIdAndOrgId(UUID quoteId, UUID orgId);
    void deleteByQuoteRefAndOrgId(String quoteRef, UUID orgId);

    // Count items
    long countByQuoteRefAndOrgId(String quoteRef, UUID orgId);
    long countByOrgId(UUID orgId);

    // Custom queries
    @Query("SELECT SUM(qd.itemValue) FROM QuoteDetail qd " +
            "WHERE qd.quoteRef = :quoteRef AND qd.orgId = :orgId")
    BigDecimal calculateTotalValueByQuoteRef(
            @Param("quoteRef") String quoteRef,
            @Param("orgId") UUID orgId
    );

    @Query("SELECT SUM(qd.itemQuantity) FROM QuoteDetail qd " +
            "WHERE qd.quoteRef = :quoteRef AND qd.orgId = :orgId")
    Integer calculateTotalQuantityByQuoteRef(
            @Param("quoteRef") String quoteRef,
            @Param("orgId") UUID orgId
    );
}
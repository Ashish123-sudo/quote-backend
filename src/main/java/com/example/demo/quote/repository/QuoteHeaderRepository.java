package com.example.demo.quote.repository;

import com.example.demo.quote.entity.QuoteHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteHeaderRepository extends JpaRepository<QuoteHeader, UUID> {

    // ✅ Simple lookup — no fetch, just finds the quote
    Optional<QuoteHeader> findByQuoteIdAndOrgId(UUID quoteId, UUID orgId);

    // ✅ Fetch with details only (use when you need line items)
    @Query("SELECT q FROM QuoteHeader q " +
            "LEFT JOIN FETCH q.quoteDetails " +
            "WHERE q.quoteId = :quoteId AND q.orgId = :orgId")
    Optional<QuoteHeader> findByIdWithDetails(
            @Param("quoteId") UUID quoteId,
            @Param("orgId") UUID orgId
    );

    // ✅ Fetch with terms only — separate query avoids MultipleBagFetchException
    @Query("SELECT q FROM QuoteHeader q " +
            "LEFT JOIN FETCH q.quoteTermsConditions " +
            "WHERE q.quoteId = :quoteId AND q.orgId = :orgId")
    Optional<QuoteHeader> findByIdWithTerms(
            @Param("quoteId") UUID quoteId,
            @Param("orgId") UUID orgId
    );

    // ✅ List queries — no fetch needed, LAZY is fine for lists
    Optional<QuoteHeader> findByQuoteRefAndOrgId(String quoteRef, UUID orgId);
    List<QuoteHeader> findByOrgId(UUID orgId);
    List<QuoteHeader> findByCustomer_CustomerIdAndOrgId(UUID customerId, UUID orgId);
    List<QuoteHeader> findByQuoteDateAndOrgId(LocalDate quoteDate, UUID orgId);
    List<QuoteHeader> findByQuoteDateBetweenAndOrgId(LocalDate startDate, LocalDate endDate, UUID orgId);
    List<QuoteHeader> findByApprovalStatusAndOrgId(String approvalStatus, UUID orgId);
    List<QuoteHeader> findByOrgIdAndApprovalStatusIn(UUID orgId, List<String> statuses);
    List<QuoteHeader> findBySubmittedByAndOrgId(String submittedBy, UUID orgId);

    long countByQuoteDateAndOrgId(LocalDate quoteDate, UUID orgId);
    long countByOrgId(UUID orgId);
    long countByApprovalStatusAndOrgId(String approvalStatus, UUID orgId);
    boolean existsByQuoteRefAndOrgId(String quoteRef, UUID orgId);

    @Query("SELECT qh FROM QuoteHeader qh WHERE qh.orgId = :orgId " +
            "AND qh.quoteDate BETWEEN :startDate AND :endDate " +
            "AND qh.approvalStatus = :status")
    List<QuoteHeader> findQuotesByDateRangeAndStatus(
            @Param("orgId") UUID orgId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status);

    @Query("SELECT qh FROM QuoteHeader qh WHERE qh.orgId = :orgId " +
            "AND qh.totalValue > :minValue ORDER BY qh.totalValue DESC")
    List<QuoteHeader> findHighValueQuotes(
            @Param("orgId") UUID orgId,
            @Param("minValue") BigDecimal minValue);

    @Query("SELECT q FROM QuoteHeader q LEFT JOIN FETCH q.customer WHERE q.orgId = :orgId")
    List<QuoteHeader> findByOrgIdWithCustomer(@Param("orgId") UUID orgId);

    @Query("SELECT q FROM QuoteHeader q LEFT JOIN FETCH q.quoteDetails WHERE q.orgId = :orgId")
    List<QuoteHeader> findByOrgIdWithDetails(@Param("orgId") UUID orgId);

    @Query("SELECT q FROM QuoteHeader q LEFT JOIN FETCH q.quoteTermsConditions WHERE q.orgId = :orgId")
    List<QuoteHeader> findByOrgIdWithTerms(@Param("orgId") UUID orgId);

    @Query("SELECT q FROM QuoteHeader q LEFT JOIN FETCH q.customer WHERE q.quoteId = :quoteId AND q.orgId = :orgId")
    Optional<QuoteHeader> findByIdWithCustomer(@Param("quoteId") UUID quoteId, @Param("orgId") UUID orgId);

    @Query("SELECT q FROM QuoteHeader q LEFT JOIN FETCH q.customer WHERE q.quoteRef = :quoteRef AND q.orgId = :orgId")
    Optional<QuoteHeader> findByQuoteRefAndOrgIdWithCustomer(@Param("quoteRef") String quoteRef, @Param("orgId") UUID orgId);

    @Query("SELECT q FROM QuoteHeader q LEFT JOIN FETCH q.customer WHERE q.customer.customerId = :customerId AND q.orgId = :orgId")
    List<QuoteHeader> findByCustomerIdAndOrgIdWithCustomer(@Param("customerId") UUID customerId, @Param("orgId") UUID orgId);

    @Query("SELECT qh FROM QuoteHeader qh WHERE qh.orgId = :orgId " +
            "AND qh.quoteDate >= :sinceDate ORDER BY qh.quoteDate DESC")
    List<QuoteHeader> findRecentQuotes(
            @Param("orgId") UUID orgId,
            @Param("sinceDate") LocalDate sinceDate);
}
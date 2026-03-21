package com.example.demo.quote.service;

import com.example.demo.quote.entity.QuoteDetail;
import com.example.demo.quote.entity.QuoteHeader;
import com.example.demo.quote.entity.QuoteTermsCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuoteService {

    // Get all quotes for an organization
    List<QuoteHeader> getAllQuotes(UUID orgId);

    // Get quote by ID with org validation
    Optional<QuoteHeader> getQuoteById(UUID quoteId, UUID orgId);

    // Get quote by reference
    Optional<QuoteHeader> getQuoteByRef(String quoteRef, UUID orgId);

    // Update quote terms
    void updateQuoteTerms(UUID quoteId, List<QuoteTermsCondition> terms, UUID orgId, UUID userId);

    // Get quotes by customer
    List<QuoteHeader> getQuotesByCustomerId(UUID customerId, UUID orgId);

    // Create new quote
    QuoteHeader createQuote(QuoteHeader quoteHeader, UUID orgId, UUID userId);

    // Update quote
    QuoteHeader updateQuote(UUID quoteId, QuoteHeader quoteHeader, UUID orgId, UUID userId);

    // Delete full quote
    void deleteQuote(UUID quoteId, UUID orgId);

    // Add single quote detail
    QuoteDetail addQuoteDetail(QuoteDetail quoteDetail, UUID orgId, UUID userId);

    // Update single quote detail
    QuoteDetail updateQuoteDetail(UUID slNo, QuoteDetail quoteDetail, UUID orgId, UUID userId);

    // Delete single quote detail
    void deleteQuoteDetail(UUID slNo, UUID orgId);

    // Generate next quote reference
    String generateNextQuoteRef(UUID orgId);
}
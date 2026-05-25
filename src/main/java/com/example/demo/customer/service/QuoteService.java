package com.example.demo.quote.service;

import com.example.demo.quote.entity.QuoteDetail;
import com.example.demo.quote.entity.QuoteHeader;
import com.example.demo.quote.entity.QuoteTermsCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuoteService {

    List<QuoteHeader> getAllQuotes(UUID orgId);
    Optional<QuoteHeader> getQuoteById(UUID quoteId, UUID orgId);
    Optional<QuoteHeader> getQuoteByRef(String quoteRef, UUID orgId);
    void updateQuoteTerms(UUID quoteId, List<QuoteTermsCondition> terms, UUID orgId, UUID userId);
    List<QuoteHeader> getQuotesByCustomerId(UUID customerId, UUID orgId);
    QuoteHeader createQuote(QuoteHeader quoteHeader, UUID orgId, UUID userId);
    QuoteHeader updateQuote(UUID quoteId, QuoteHeader quoteHeader, UUID orgId, UUID userId);
    void deleteQuote(UUID quoteId, UUID orgId);
    QuoteDetail addQuoteDetail(QuoteDetail quoteDetail, UUID orgId, UUID userId);
    QuoteDetail updateQuoteDetail(UUID slNo, QuoteDetail quoteDetail, UUID orgId, UUID userId);
    void deleteQuoteDetail(UUID slNo, UUID orgId);

    // Increments serial and saves — called only on actual quote creation
    String generateNextQuoteRef(UUID orgId);

    // Read-only preview — does NOT increment serial
    String peekNextQuoteRef(UUID orgId);
}
package com.example.demo.quote.service;

import com.example.demo.quote.entity.QuoteDetail;
import com.example.demo.quote.entity.QuoteHeader;

import java.util.List;
import java.util.Optional;

public interface QuoteService {

    // Get all quotes
    List<QuoteHeader> getAllQuotes();

    // Get quote by ID (with details)
    Optional<QuoteHeader> getQuoteById(Long id);

    // Get quote by reference
    Optional<QuoteHeader> getQuoteByRef(String quoteRef);

    // Get quotes by customer
    List<QuoteHeader> getQuotesByCustomerId(Integer customerId);

    // Create new quote
    QuoteHeader createQuote(QuoteHeader quoteHeader);

    // Update quote
    QuoteHeader updateQuote(Long id, QuoteHeader quoteHeader);

    // Delete full quote
    void deleteQuote(Long id);

    // Add single quote detail
    QuoteDetail addQuoteDetail(QuoteDetail quoteDetail);

    // Update single quote detail
    QuoteDetail updateQuoteDetail(Long slNo, QuoteDetail quoteDetail);

    // Delete single quote detail
    void deleteQuoteDetail(Long slNo);

    String generateNextQuoteRef();
}
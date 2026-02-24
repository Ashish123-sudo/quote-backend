package com.example.demo.quote.repository;

import com.example.demo.quote.entity.QuoteDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteDetailRepository extends JpaRepository<QuoteDetail, Long> {

    // Find all details for a quote reference
    List<QuoteDetail> findByQuoteRef(String quoteRef);

    // Find all details for a quote header
    List<QuoteDetail> findByQuoteHeader_QuoteId(Long quoteId);
}
package com.example.demo.quote.repository;

import com.example.demo.quote.entity.QuoteHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface QuoteHeaderRepository extends JpaRepository<QuoteHeader, Long> {

    Optional<QuoteHeader> findByQuoteRef(String quoteRef);

    List<QuoteHeader> findByCustomerId(Integer customerId);

    List<QuoteHeader> findByQuoteDateBetween(LocalDate startDate, LocalDate endDate);

    List<QuoteHeader> findByQuoteDate(LocalDate quoteDate);

    // ✅ NEW
    long countByQuoteDate(LocalDate quoteDate);
}
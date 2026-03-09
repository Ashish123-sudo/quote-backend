package com.example.demo.quote.repository;

import com.example.demo.quote.entity.QuoteTermsCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuoteTermsConditionRepository extends JpaRepository<QuoteTermsCondition, Long> {
    List<QuoteTermsCondition> findByQuoteHeader_QuoteIdOrderByGroupOrderAscTermOrderAsc(Long quoteId);
    void deleteByQuoteHeader_QuoteId(Long quoteId);
}
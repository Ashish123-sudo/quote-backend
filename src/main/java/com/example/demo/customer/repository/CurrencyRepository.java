package com.example.demo.customer.repository;

import com.example.demo.customer.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    boolean existsByCurrencyCode(String currencyCode);
    Optional<Currency> findByIsDefaultTrue();
}
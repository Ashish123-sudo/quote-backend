package com.example.demo.customer.repository;

import com.example.demo.customer.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    // Find by ID with org_id filtering
    Optional<Currency> findByCurrencyIdAndOrgId(UUID currencyId, UUID orgId);

    // Find by currency code
    Optional<Currency> findByCurrencyCodeAndOrgId(String currencyCode, UUID orgId);

    // Find all currencies for an organization
    List<Currency> findByOrgId(UUID orgId);

    // Find default currency
    Optional<Currency> findByOrgIdAndIsDefaultTrue(UUID orgId);

    // Check existence
    boolean existsByCurrencyCodeAndOrgId(String currencyCode, UUID orgId);

    // Count currencies
    long countByOrgId(UUID orgId);
}
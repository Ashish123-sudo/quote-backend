package com.example.demo.customer.repository;

import com.example.demo.customer.entity.QuoteRefConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface QuoteRefConfigRepository extends JpaRepository<QuoteRefConfig, UUID> {
    Optional<QuoteRefConfig> findByOrgId(UUID orgId);
}
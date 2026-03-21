package com.example.demo.customer.repository;

import com.example.demo.customer.entity.TcType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TcTypeRepository extends JpaRepository<TcType, UUID> {

    // Find by ID with org_id filtering
    Optional<TcType> findByTypeIdAndOrgId(UUID typeId, UUID orgId);

    // Find by type name
    Optional<TcType> findByTypeNameAndOrgId(String typeName, UUID orgId);

    // Find all types for an organization
    List<TcType> findByOrgId(UUID orgId);

    // Check existence
    boolean existsByTypeNameAndOrgId(String typeName, UUID orgId);

    // Count types
    long countByOrgId(UUID orgId);
}
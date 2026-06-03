package com.example.demo.customer.repository;

import com.example.demo.customer.entity.TcLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface TcLibraryRepository extends JpaRepository<TcLibrary, UUID> {

    // Find by ID with org_id filtering
    Optional<TcLibrary> findByTermIdAndOrgId(UUID termId, UUID orgId);

    // Find all terms for an organization
    List<TcLibrary> findByOrgId(UUID orgId);

    // Find by type
    List<TcLibrary> findByTcType_TypeIdAndOrgId(UUID typeId, UUID orgId);
    List<TcLibrary> findByTcType_TypeNameAndOrgId(String typeName, UUID orgId);

    // Find by text content
    List<TcLibrary> findByTermTextContainingIgnoreCaseAndOrgId(String searchText, UUID orgId);

    // Find ordered by sort_order
    List<TcLibrary> findByOrgIdOrderBySortOrderAsc(UUID orgId);
    List<TcLibrary> findByTcType_TypeIdAndOrgIdOrderBySortOrderAsc(UUID typeId, UUID orgId);

    // Count terms
    long countByOrgId(UUID orgId);
    long countByTcType_TypeIdAndOrgId(UUID typeId, UUID orgId);

    @Query("SELECT t FROM TcLibrary t LEFT JOIN FETCH t.tcType WHERE t.orgId = :orgId ORDER BY t.sortOrder ASC")
    List<TcLibrary> findByOrgIdWithTypeOrderBySortOrderAsc(@Param("orgId") UUID orgId);
}
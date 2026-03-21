package com.example.demo.customer.repository;

import com.example.demo.customer.entity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, UUID> {

    // Find by ID with org_id filtering
    Optional<AppRole> findByRoleIdAndOrgId(UUID roleId, UUID orgId);

    // Find by role name
    Optional<AppRole> findByRoleNameAndOrgId(String roleName, UUID orgId);

    // Find all roles in an organization
    List<AppRole> findByOrgId(UUID orgId);

    // Check existence
    boolean existsByRoleNameAndOrgId(String roleName, UUID orgId);
    boolean existsByRoleIdAndOrgId(UUID roleId, UUID orgId);

    // Count roles
    long countByOrgId(UUID orgId);
}
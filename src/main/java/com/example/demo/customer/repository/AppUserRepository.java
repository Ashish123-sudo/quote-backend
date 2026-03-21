package com.example.demo.customer.repository;

import com.example.demo.customer.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    // Find by ID with org_id filtering
    Optional<AppUser> findByUserIdAndOrgId(UUID userId, UUID orgId);

    // Find by username (unique per org)
    Optional<AppUser> findByUsernameAndOrgId(String username, UUID orgId);

    // Find by email
    Optional<AppUser> findByEmailAndOrgId(String email, UUID orgId);

    // Find all users in an organization
    List<AppUser> findByOrgId(UUID orgId);

    // Find active users only
    List<AppUser> findByOrgIdAndIsActiveTrue(UUID orgId);

    // Find by role
    List<AppUser> findByAppRole_RoleIdAndOrgId(UUID roleId, UUID orgId);
    List<AppUser> findByAppRole_RoleNameAndOrgId(String roleName, UUID orgId);

    // Existence checks
    boolean existsByUsernameAndOrgId(String username, UUID orgId);
    boolean existsByEmailAndOrgId(String email, UUID orgId);
    boolean existsByUserIdAndOrgId(UUID userId, UUID orgId);

    // Find by username for login (without org filtering)
    List<AppUser> findByUsername(String username);

    // Count queries
    long countByOrgId(UUID orgId);
    long countByOrgIdAndIsActiveTrue(UUID orgId);
    long countByAppRole_RoleIdAndOrgId(UUID roleId, UUID orgId);
}
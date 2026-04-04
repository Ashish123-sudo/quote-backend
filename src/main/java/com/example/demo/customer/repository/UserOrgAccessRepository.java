package com.example.demo.customer.repository;

import com.example.demo.customer.entity.UserOrgAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserOrgAccessRepository extends JpaRepository<UserOrgAccess, UUID> {

    List<UserOrgAccess> findByUserIdAndIsActiveTrue(UUID userId);

    boolean existsByUserIdAndOrgIdAndIsActiveTrue(UUID userId, UUID orgId);

    @Query("SELECT u FROM UserOrgAccess u WHERE u.userId = :userId AND u.orgId = :orgId AND u.isActive = true")
    java.util.Optional<UserOrgAccess> findByUserIdAndOrgId(@Param("userId") UUID userId, @Param("orgId") UUID orgId);
}
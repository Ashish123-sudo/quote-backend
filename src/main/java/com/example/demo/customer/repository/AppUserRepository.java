package com.example.demo.customer.repository;

import com.example.demo.customer.entity.AppRole;
import com.example.demo.customer.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    AppUser findByUsername(String username);

    List<AppUser> findByOrgId(UUID orgId);

    List<AppUser> findByOrgIdAndAppRole(UUID orgId, AppRole appRole);

    Optional<AppUser> findByUserIdAndOrgId(UUID userId, UUID orgId);

    boolean existsByUsernameAndOrgId(String username, UUID orgId);

    boolean existsByUserIdAndOrgId(UUID userId, UUID orgId);

    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.appRole WHERE u.username = :username")
    AppUser findByUsernameWithRole(@Param("username") String username);

}
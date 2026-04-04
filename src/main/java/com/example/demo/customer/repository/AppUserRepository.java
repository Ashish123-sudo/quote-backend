package com.example.demo.customer.repository;

import com.example.demo.customer.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    AppUser findByUsername(String username);

    List<AppUser> findByOrgId(UUID orgId);

    Optional<AppUser> findByUserIdAndOrgId(UUID userId, UUID orgId);

    boolean existsByUsernameAndOrgId(String username, UUID orgId);

    boolean existsByUserIdAndOrgId(UUID userId, UUID orgId);
}
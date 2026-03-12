package com.example.demo.customer.repository;

import com.example.demo.customer.entity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    boolean existsByRoleName(String roleName);
}
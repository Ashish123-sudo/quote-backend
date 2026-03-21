package com.example.demo.customer.repository;

import com.example.demo.customer.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    // Find by organization code
    Optional<Organization> findByOrgCode(String orgCode);

    // Find by organization name
    Optional<Organization> findByOrgName(String orgName);
    List<Organization> findByOrgNameContainingIgnoreCase(String orgName);

    // Find active organizations
    List<Organization> findByIsActiveTrue();

    // Find by location
    List<Organization> findByCity(String city);
    List<Organization> findByCountry(String country);
    List<Organization> findByCityAndCountry(String city, String country);

    // Check existence
    boolean existsByOrgCode(String orgCode);
    boolean existsByOrgName(String orgName);

    // Count organizations
    long countByIsActiveTrue();
}
package com.example.demo.customer.repository;

import com.example.demo.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    // Basic finders with org_id filtering for multi-tenancy
    Optional<Customer> findByCustomerIdAndOrgId(UUID customerId, UUID orgId);
    List<Customer> findByOrgId(UUID orgId);

    // Search by name
    List<Customer> findByNameAndOrgId(String name, UUID orgId);
    List<Customer> findByNameContainingIgnoreCaseAndOrgId(String name, UUID orgId);

    // Search by location
    List<Customer> findByCityAndOrgId(String city, UUID orgId);
    List<Customer> findByCountryAndOrgId(String country, UUID orgId);
    List<Customer> findByCityAndCountryAndOrgId(String city, String country, UUID orgId);

    // Active customers only
    List<Customer> findByOrgIdAndIsActiveTrue(UUID orgId);

    // Check if customer exists
    boolean existsByCustomerIdAndOrgId(UUID customerId, UUID orgId);
    boolean existsByEmailIdAndOrgId(String emailId, UUID orgId);

    // Count customers
    long countByOrgId(UUID orgId);
    long countByOrgIdAndIsActiveTrue(UUID orgId);
}
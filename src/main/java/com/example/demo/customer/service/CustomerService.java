package com.example.demo.customer.service;

import com.example.demo.customer.entity.Customer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    // Get all customers for an organization
    List<Customer> getAllCustomers(UUID orgId);

    // Get customer by ID with org validation
    Optional<Customer> getCustomerById(UUID customerId, UUID orgId);

    // Create new customer
    Customer createCustomer(Customer customer, UUID orgId, UUID userId);

    // Update existing customer
    Customer updateCustomer(UUID customerId, Customer customer, UUID orgId, UUID userId);

    // Delete customer
    void deleteCustomer(UUID customerId, UUID orgId);

    // Search customers by name within organization
    List<Customer> searchByName(String name, UUID orgId);

    // Get active customers only
    List<Customer> getActiveCustomers(UUID orgId);

    // Check if customer exists
    boolean existsById(UUID customerId, UUID orgId);
}
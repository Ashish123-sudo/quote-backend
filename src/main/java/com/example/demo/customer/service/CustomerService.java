package com.example.demo.customer.service;

import com.example.demo.customer.entity.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {

    // Get all customers
    List<Customer> getAllCustomers();

    // Get customer by ID
    Optional<Customer> getCustomerById(Integer id);

    // Create new customer
    Customer createCustomer(Customer customer);

    // Update existing customer
    Customer updateCustomer(Integer id, Customer customer);

    // Delete customer
    void deleteCustomer(Integer id);

    // Search customers by name
    List<Customer> searchByName(String name);
}
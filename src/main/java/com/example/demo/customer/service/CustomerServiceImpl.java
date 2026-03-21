package com.example.demo.customer.service;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> getAllCustomers(UUID orgId) {
        return customerRepository.findByOrgId(orgId);
    }

    @Override
    public Optional<Customer> getCustomerById(UUID customerId, UUID orgId) {
        return customerRepository.findByCustomerIdAndOrgId(customerId, orgId);
    }

    @Override
    public Customer createCustomer(Customer customer, UUID orgId, UUID userId) {
        // Set organization and audit fields
        customer.setOrgId(orgId);
        customer.setCreatedBy(userId);
        customer.setUpdatedBy(userId);

        // Set default active status if not provided
        if (customer.getIsActive() == null) {
            customer.setIsActive(true);
        }

        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(UUID customerId, Customer customer, UUID orgId, UUID userId) {
        // Find existing customer with org validation
        Customer existing = customerRepository.findByCustomerIdAndOrgId(customerId, orgId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        // Update fields
        existing.setName(customer.getName());
        existing.setAddress1(customer.getAddress1());
        existing.setAddress2(customer.getAddress2());
        existing.setCity(customer.getCity());
        existing.setStateProvince(customer.getStateProvince());
        existing.setCountry(customer.getCountry());
        existing.setContactNumber(customer.getContactNumber());
        existing.setEmailId(customer.getEmailId());
        existing.setWebUrl(customer.getWebUrl());

        if (customer.getIsActive() != null) {
            existing.setIsActive(customer.getIsActive());
        }

        // Set audit field
        existing.setUpdatedBy(userId);

        return customerRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID customerId, UUID orgId) {
        System.out.println("🗑️ CustomerService: Attempting to delete customer ID: " + customerId);

        // Verify customer exists and belongs to org
        if (!customerRepository.existsByCustomerIdAndOrgId(customerId, orgId)) {
            System.err.println("❌ CustomerService: Customer not found with ID: " + customerId);
            throw new RuntimeException("Customer not found with id: " + customerId);
        }

        try {
            Customer customer = customerRepository.findByCustomerIdAndOrgId(customerId, orgId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            customerRepository.delete(customer);
            customerRepository.flush();
            System.out.println("✅ CustomerService: Successfully deleted customer ID: " + customerId);
        } catch (DataIntegrityViolationException e) {
            System.err.println("❌ CustomerService: Cannot delete customer ID " + customerId + " - has related quotes");
            throw new DataIntegrityViolationException(
                    "Cannot delete customer - they have existing quotes. Please delete the quotes first."
            );
        }
    }

    @Override
    public List<Customer> searchByName(String name, UUID orgId) {
        return customerRepository.findByNameContainingIgnoreCaseAndOrgId(name, orgId);
    }

    @Override
    public List<Customer> getActiveCustomers(UUID orgId) {
        return customerRepository.findByOrgIdAndIsActiveTrue(orgId);
    }

    @Override
    public boolean existsById(UUID customerId, UUID orgId) {
        return customerRepository.existsByCustomerIdAndOrgId(customerId, orgId);
    }
}
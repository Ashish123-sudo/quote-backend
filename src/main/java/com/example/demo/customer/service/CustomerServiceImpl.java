package com.example.demo.customer.service;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomerById(Integer id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Integer id, Customer customer) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customer.setCustomerId(id);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Integer id) {
        System.out.println("🗑️ CustomerService: Attempting to delete customer ID: " + id);

        if (!customerRepository.existsById(id)) {
            System.err.println("❌ CustomerService: Customer not found with ID: " + id);
            throw new RuntimeException("Customer not found with id: " + id);
        }

        try {
            customerRepository.deleteById(id);
            customerRepository.flush();
            System.out.println("✅ CustomerService: Successfully deleted customer ID: " + id);
        } catch (DataIntegrityViolationException e) {
            System.err.println("❌ CustomerService: Cannot delete customer ID " + id + " - has related quotes");
            throw new DataIntegrityViolationException(
                    "Cannot delete customer - they have existing quotes. Please delete the quotes first."
            );
        }
    }

    @Override
    public List<Customer> searchByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }
}
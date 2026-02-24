package com.example.demo.customer.controller;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error fetching customers: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        try {
            Customer createdCustomer = customerService.createCustomer(customer);
            return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error creating customer: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer id,
                                                   @RequestBody Customer customer) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error updating customer: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Integer id) {
        try {
            System.out.println("🌐 DELETE /api/customers/" + id + " - Request received");
            customerService.deleteCustomer(id);
            System.out.println("✅ DELETE /api/customers/" + id + " - Success");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (DataIntegrityViolationException e) {
            System.err.println("❌ DELETE /api/customers/" + id + " - Foreign key constraint");

            // Return detailed error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "FOREIGN_KEY_CONSTRAINT");
            errorResponse.put("message", "Cannot delete customer - they have existing quotes. Please delete the quotes first.");
            errorResponse.put("customerId", id.toString());

            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

        } catch (RuntimeException e) {
            System.err.println("❌ DELETE /api/customers/" + id + " - Customer not found");

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "NOT_FOUND");
            errorResponse.put("message", "Customer not found with id: " + id);

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            System.err.println("❌ DELETE /api/customers/" + id + " - Error: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "INTERNAL_ERROR");
            errorResponse.put("message", "An unexpected error occurred");

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String name) {
        try {
            List<Customer> customers = customerService.searchByName(name);
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error searching customers: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
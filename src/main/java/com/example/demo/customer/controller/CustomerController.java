package com.example.demo.customer.controller;

import com.example.demo.config.SecurityHelper;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SecurityHelper securityHelper;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            List<Customer> customers = customerService.getAllCustomers(orgId);
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error fetching customers: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            Optional<Customer> customer = customerService.getCustomerById(id, orgId);
            return customer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            System.err.println("❌ Error fetching customer: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();
            Customer createdCustomer = customerService.createCustomer(customer, orgId, userId);
            return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error creating customer: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable UUID id,
                                                   @RequestBody Customer customer) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();
            Customer updatedCustomer = customerService.updateCustomer(id, customer, orgId, userId);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        } catch (RuntimeException e) {
            System.err.println("❌ Error updating customer: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            System.out.println("🌐 DELETE /api/customers/" + id + " - Request received");
            customerService.deleteCustomer(id, orgId);
            System.out.println("✅ DELETE /api/customers/" + id + " - Success");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (DataIntegrityViolationException e) {
            System.err.println("❌ DELETE /api/customers/" + id + " - Foreign key constraint");

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
            UUID orgId = securityHelper.getCurrentOrgId();
            List<Customer> customers = customerService.searchByName(name, orgId);
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error searching customers: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<Customer>> getActiveCustomers() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            List<Customer> customers = customerService.getActiveCustomers(orgId);
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error fetching active customers: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
package com.example.demo.customer.controller;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.entity.Currency;
import com.example.demo.customer.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/currencies")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class CurrencyController {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private SecurityHelper securityHelper;

    @GetMapping
    public ResponseEntity<List<Currency>> getAll() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            List<Currency> currencies = currencyRepository.findByOrgId(orgId);
            return ResponseEntity.ok(currencies);
        } catch (Exception e) {
            System.err.println("❌ Error fetching currencies: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Currency currency) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            String currencyCode = currency.getCurrencyCode().toUpperCase();

            // Check if currency code already exists in this org
            if (currencyRepository.existsByCurrencyCodeAndOrgId(currencyCode, orgId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Currency code '" + currencyCode + "' already exists"));
            }

            // Set org and audit fields
            currency.setOrgId(orgId);
            currency.setCurrencyCode(currencyCode);
            currency.setCreatedBy(userId);
            currency.setUpdatedBy(userId);

            // If this is the first currency for the org, make it default
            if (currencyRepository.countByOrgId(orgId) == 0) {
                currency.setIsDefault(true);
            }

            Currency savedCurrency = currencyRepository.save(currency);
            return new ResponseEntity<>(savedCurrency, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error creating currency: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Currency currency) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            Currency existing = currencyRepository.findByCurrencyIdAndOrgId(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Currency not found"));

            existing.setCurrencyCode(currency.getCurrencyCode().toUpperCase());
            existing.setCurrencyName(currency.getCurrencyName());
            existing.setUpdatedBy(userId);

            Currency updated = currencyRepository.save(existing);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            System.err.println("❌ Error updating currency: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ Error updating currency: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();

            Currency existing = currencyRepository.findByCurrencyIdAndOrgId(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Currency not found"));

            // Prevent deleting default currency
            if (Boolean.TRUE.equals(existing.getIsDefault())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot delete the default currency"));
            }

            currencyRepository.delete(existing);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            System.err.println("❌ Error deleting currency: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ Error deleting currency: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/set-default")
    public ResponseEntity<?> setDefault(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            // Clear existing default for this org
            currencyRepository.findByOrgIdAndIsDefaultTrue(orgId).ifPresent(c -> {
                c.setIsDefault(false);
                c.setUpdatedBy(userId);
                currencyRepository.save(c);
            });

            // Set new default
            Currency currency = currencyRepository.findByCurrencyIdAndOrgId(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Currency not found"));

            currency.setIsDefault(true);
            currency.setUpdatedBy(userId);

            Currency updated = currencyRepository.save(currency);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            System.err.println("❌ Error setting default currency: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ Error setting default currency: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
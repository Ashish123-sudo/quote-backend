package com.example.demo.customer.controller;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.entity.Currency;
import com.example.demo.customer.repository.CurrencyRepository;
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

    private final CurrencyRepository currencyRepository;
    private final SecurityHelper securityHelper;

    public CurrencyController(CurrencyRepository currencyRepository,
                              SecurityHelper securityHelper) {
        this.currencyRepository = currencyRepository;
        this.securityHelper     = securityHelper;
    }

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

            if (currencyRepository.existsByCurrencyCodeAndOrgId(currencyCode, orgId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Currency code '" + currencyCode + "' already exists"));
            }

            currency.setOrgId(orgId);
            currency.setCurrencyCode(currencyCode);
            currency.setCreatedBy(userId);
            currency.setUpdatedBy(userId);

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

            currencyRepository.findByOrgIdAndIsDefaultTrue(orgId).ifPresent(c -> {
                c.setIsDefault(false);
                c.setUpdatedBy(userId);
                currencyRepository.save(c);
            });

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
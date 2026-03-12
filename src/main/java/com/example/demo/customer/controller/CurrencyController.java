package com.example.demo.customer.controller;

import com.example.demo.customer.entity.Currency;
import com.example.demo.customer.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/currencies")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class CurrencyController {

    @Autowired private CurrencyRepository currencyRepository;

    @GetMapping
    public List<Currency> getAll() {
        return currencyRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Currency currency) {
        if (currencyRepository.existsByCurrencyCode(currency.getCurrencyCode().toUpperCase())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Currency code already exists"));
        }
        currency.setCurrencyCode(currency.getCurrencyCode().toUpperCase());
        // If this is the first currency, make it default
        if (currencyRepository.count() == 0) currency.setIsDefault(true);
        return new ResponseEntity<>(currencyRepository.save(currency), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Currency currency) {
        Currency existing = currencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Currency not found"));
        existing.setCurrencyCode(currency.getCurrencyCode().toUpperCase());
        existing.setCurrencyName(currency.getCurrencyName());
        return ResponseEntity.ok(currencyRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Currency existing = currencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Currency not found"));
        if (Boolean.TRUE.equals(existing.getIsDefault())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot delete the default currency"));
        }
        currencyRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/set-default")
    public ResponseEntity<?> setDefault(@PathVariable Long id) {
        // Clear existing default
        currencyRepository.findByIsDefaultTrue().ifPresent(c -> {
            c.setIsDefault(false);
            currencyRepository.save(c);
        });
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Currency not found"));
        currency.setIsDefault(true);
        return ResponseEntity.ok(currencyRepository.save(currency));
    }
}
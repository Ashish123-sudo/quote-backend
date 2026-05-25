package com.example.demo.customer.controller;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.entity.QuoteRefConfig;
import com.example.demo.customer.repository.QuoteRefConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/quote-ref-config")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class QuoteRefConfigController {

    @Autowired private QuoteRefConfigRepository configRepository;
    @Autowired private SecurityHelper securityHelper;

    @GetMapping
    public ResponseEntity<QuoteRefConfig> getConfig() {
        UUID orgId = securityHelper.getCurrentOrgId();
        QuoteRefConfig config = configRepository.findByOrgId(orgId)
                .orElseGet(() -> defaultConfig(orgId));
        return ResponseEntity.ok(config);
    }

    @PostMapping
    public ResponseEntity<QuoteRefConfig> saveConfig(@RequestBody QuoteRefConfig config) {
        UUID orgId = securityHelper.getCurrentOrgId();
        UUID userId = securityHelper.getCurrentUserId();

        QuoteRefConfig existing = configRepository.findByOrgId(orgId)
                .orElseGet(() -> {
                    QuoteRefConfig c = new QuoteRefConfig();
                    c.setOrgId(orgId);
                    c.setCreatedBy(userId);
                    c.setLastSerial(0);
                    return c;
                });

        existing.setPrefix(config.getPrefix());
        existing.setSeparator(config.getSeparator());
        existing.setDateFormat(config.getDateFormat());
        existing.setSerialDigits(config.getSerialDigits());
        existing.setResetType(config.getResetType());
        existing.setResetAtValue(config.getResetAtValue());
        existing.setSuffix(config.getSuffix());
        existing.setUpdatedBy(userId);
        existing.setLastSerial(config.getLastSerial());

        return ResponseEntity.ok(configRepository.save(existing));
    }

    private QuoteRefConfig defaultConfig(UUID orgId) {
        QuoteRefConfig c = new QuoteRefConfig();
        c.setOrgId(orgId);
        return c;
    }
}
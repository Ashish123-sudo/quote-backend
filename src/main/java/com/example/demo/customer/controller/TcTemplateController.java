package com.example.demo.customer.controller;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.entity.TcLibrary;
import com.example.demo.customer.entity.TcTemplate;
import com.example.demo.customer.repository.TcLibraryRepository;
import com.example.demo.customer.repository.TcTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tc/templates")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class TcTemplateController {

    @Autowired
    private TcTemplateRepository tcTemplateRepository;

    @Autowired
    private TcLibraryRepository tcLibraryRepository;

    @Autowired
    private SecurityHelper securityHelper;

    @GetMapping
    public ResponseEntity<List<TcTemplate>> getAll() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            List<TcTemplate> templates = tcTemplateRepository.findByOrgId(orgId);
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            System.err.println("❌ Error fetching templates: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TcTemplate> getById(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            return tcTemplateRepository.findByTemplateIdAndOrgId(id, orgId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("❌ Error fetching template: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<TcTemplate> create(@RequestBody Map<String, Object> payload) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            TcTemplate template = new TcTemplate();
            template.setOrgId(orgId);
            template.setTemplateName((String) payload.get("templateName"));
            template.setCreatedBy(userId);
            template.setUpdatedBy(userId);

            // Handle termIds - they should be UUIDs now
            List<?> termIdsRaw = (List<?>) payload.get("termIds");
            if (termIdsRaw != null && !termIdsRaw.isEmpty()) {
                List<TcLibrary> terms = termIdsRaw.stream()
                        .map(id -> {
                            UUID termId = UUID.fromString(id.toString());
                            return tcLibraryRepository.findByTermIdAndOrgId(termId, orgId)
                                    .orElseThrow(() -> new RuntimeException("Term not found: " + termId));
                        })
                        .collect(Collectors.toList());
                template.setTerms(terms);
            }

            TcTemplate saved = tcTemplateRepository.save(template);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error creating template: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TcTemplate> update(@PathVariable UUID id,
                                             @RequestBody Map<String, Object> payload) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            TcTemplate template = tcTemplateRepository.findByTemplateIdAndOrgId(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Template not found"));

            template.setTemplateName((String) payload.get("templateName"));
            template.setUpdatedBy(userId);

            // Handle termIds - they should be UUIDs now
            List<?> termIdsRaw = (List<?>) payload.get("termIds");
            if (termIdsRaw != null) {
                List<TcLibrary> terms = termIdsRaw.stream()
                        .map(id2 -> {
                            UUID termId = UUID.fromString(id2.toString());
                            return tcLibraryRepository.findByTermIdAndOrgId(termId, orgId)
                                    .orElseThrow(() -> new RuntimeException("Term not found: " + termId));
                        })
                        .collect(Collectors.toList());
                template.setTerms(terms);
            }

            TcTemplate updated = tcTemplateRepository.save(template);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            System.err.println("❌ Error updating template: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ Error updating template: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();

            TcTemplate template = tcTemplateRepository.findByTemplateIdAndOrgId(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Template not found"));

            tcTemplateRepository.delete(template);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            System.err.println("❌ Error deleting template: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ Error deleting template: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
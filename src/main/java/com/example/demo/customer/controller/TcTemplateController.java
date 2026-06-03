package com.example.demo.customer.controller;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.entity.TcLibrary;
import com.example.demo.customer.entity.TcTemplate;
import com.example.demo.customer.repository.TcLibraryRepository;
import com.example.demo.customer.repository.TcTemplateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.customer.entity.TcTemplateItem;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
@RestController
@RequestMapping("/api/tc/templates")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class TcTemplateController {

    private final TcTemplateRepository tcTemplateRepository;
    private final TcLibraryRepository tcLibraryRepository;
    private final SecurityHelper securityHelper;

    public TcTemplateController(TcTemplateRepository tcTemplateRepository,
                                TcLibraryRepository tcLibraryRepository,
                                SecurityHelper securityHelper) {
        this.tcTemplateRepository = tcTemplateRepository;
        this.tcLibraryRepository  = tcLibraryRepository;
        this.securityHelper       = securityHelper;
    }

    @GetMapping
    public ResponseEntity<List<TcTemplate>> getAll() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            List<TcTemplate> templates = tcTemplateRepository.findByOrgIdWithItems(orgId);
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
            return tcTemplateRepository.findByTemplateIdAndOrgIdWithItems(id, orgId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("❌ Error fetching template: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @Transactional
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

            List<?> termIdsRaw = (List<?>) payload.get("termIds");
            if (termIdsRaw != null && !termIdsRaw.isEmpty()) {
                template.getTemplateItems().clear();  // wipe existing items first (important for update)
                termIdsRaw.forEach(id -> {
                    UUID termId = UUID.fromString(id.toString());
                    TcLibrary term = tcLibraryRepository.findByTermIdAndOrgId(termId, orgId)
                            .orElseThrow(() -> new RuntimeException("Term not found: " + termId));
                    template.addTerm(term);  // ✅ uses the helper that creates TcTemplateItem correctly
                });
            }

            TcTemplate saved = tcTemplateRepository.save(template);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error creating template: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
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

            List<?> termIdsRaw = (List<?>) payload.get("termIds");
            if (termIdsRaw != null) {
                template.getTemplateItems().clear();  // wipe existing items first (important for update)
                termIdsRaw.forEach(rawId -> {
                    UUID termId = UUID.fromString(rawId.toString());
                    TcLibrary term = tcLibraryRepository.findByTermIdAndOrgId(termId, orgId)
                            .orElseThrow(() -> new RuntimeException("Term not found: " + termId));
                    template.addTerm(term);
                });
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
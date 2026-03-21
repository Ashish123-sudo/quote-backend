package com.example.demo.customer.controller;

import com.example.demo.config.SecurityHelper;
import com.example.demo.customer.entity.TcLibrary;
import com.example.demo.customer.entity.TcType;
import com.example.demo.customer.repository.TcLibraryRepository;
import com.example.demo.customer.repository.TcTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tc")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class TcLibraryController {

    @Autowired
    private TcLibraryRepository tcLibraryRepository;

    @Autowired
    private TcTypeRepository tcTypeRepository;

    @Autowired
    private SecurityHelper securityHelper;

    // ── TYPES ──────────────────────────────────────────

    @GetMapping("/types")
    public ResponseEntity<List<TcType>> getAllTypes() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            List<TcType> types = tcTypeRepository.findByOrgId(orgId);
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            System.err.println("❌ Error fetching types: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/types")
    public ResponseEntity<TcType> createType(@RequestBody TcType tcType) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            tcType.setOrgId(orgId);
            tcType.setCreatedBy(userId);
            tcType.setUpdatedBy(userId);

            TcType saved = tcTypeRepository.save(tcType);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error creating type: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();

            TcType type = tcTypeRepository.findByTypeIdAndOrgId(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Type not found"));

            tcTypeRepository.delete(type);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            System.err.println("❌ Error deleting type: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ Error deleting type: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ── TERMS ──────────────────────────────────────────

    @GetMapping("/terms")
    public ResponseEntity<List<TcLibrary>> getAllTerms() {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            // Return sorted by sortOrder
            List<TcLibrary> terms = tcLibraryRepository.findByOrgIdOrderBySortOrderAsc(orgId);
            return ResponseEntity.ok(terms);
        } catch (Exception e) {
            System.err.println("❌ Error fetching terms: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/terms")
    public ResponseEntity<TcLibrary> createTerm(@RequestBody TcLibrary term) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            term.setOrgId(orgId);
            term.setCreatedBy(userId);
            term.setUpdatedBy(userId);

            // Handle TcType - create or link
            if (term.getTcType() == null || term.getTcType().getTypeId() == null) {
                // Create or get "General" type
                TcType generalType = tcTypeRepository.findByTypeNameAndOrgId("General", orgId)
                        .orElseGet(() -> {
                            TcType t = new TcType();
                            t.setTypeName("General");
                            t.setOrgId(orgId);
                            t.setCreatedBy(userId);
                            t.setUpdatedBy(userId);
                            return tcTypeRepository.save(t);
                        });
                term.setTcType(generalType);
            } else {
                TcType type = tcTypeRepository.findByTypeIdAndOrgId(term.getTcType().getTypeId(), orgId)
                        .orElseThrow(() -> new RuntimeException("Type not found"));
                term.setTcType(type);
            }

            // If no sortOrder provided, put it at the end
            if (term.getSortOrder() == null) {
                int maxOrder = (int) tcLibraryRepository.countByOrgId(orgId);
                term.setSortOrder(maxOrder + 1);
            }

            TcLibrary saved = tcLibraryRepository.save(term);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error creating term: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/terms/{id}")
    public ResponseEntity<TcLibrary> updateTerm(@PathVariable UUID id, @RequestBody TcLibrary term) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            TcLibrary existing = tcLibraryRepository.findByTermIdAndOrgId(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));

            existing.setTermText(term.getTermText());
            existing.setUpdatedBy(userId);

            if (term.getSortOrder() != null) {
                existing.setSortOrder(term.getSortOrder());
            }

            if (term.getTcType() != null && term.getTcType().getTypeId() != null) {
                TcType type = tcTypeRepository.findByTypeIdAndOrgId(term.getTcType().getTypeId(), orgId)
                        .orElseThrow(() -> new RuntimeException("Type not found"));
                existing.setTcType(type);
            } else {
                existing.setTcType(null);
            }

            TcLibrary updated = tcLibraryRepository.save(existing);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            System.err.println("❌ Error updating term: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ Error updating term: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/terms/{id}")
    public ResponseEntity<Void> deleteTerm(@PathVariable UUID id) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();

            TcLibrary term = tcLibraryRepository.findByTermIdAndOrgId(id, orgId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));

            tcLibraryRepository.delete(term);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            System.err.println("❌ Error deleting term: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ Error deleting term: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ── REORDER ────────────────────────────────────────

    // DTO for reorder request
    static class ReorderItem {
        public String termId;  // Changed to String to accept UUID
        public Integer sortOrder;
    }

    @PutMapping("/terms/reorder")
    public ResponseEntity<Void> reorderTerms(@RequestBody List<ReorderItem> items) {
        try {
            UUID orgId = securityHelper.getCurrentOrgId();
            UUID userId = securityHelper.getCurrentUserId();

            for (ReorderItem item : items) {
                UUID termId = UUID.fromString(item.termId);
                tcLibraryRepository.findByTermIdAndOrgId(termId, orgId).ifPresent(term -> {
                    term.setSortOrder(item.sortOrder);
                    term.setUpdatedBy(userId);
                    tcLibraryRepository.save(term);
                });
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("❌ Error reordering terms: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
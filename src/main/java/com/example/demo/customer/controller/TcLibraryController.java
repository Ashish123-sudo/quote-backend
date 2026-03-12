package com.example.demo.customer.controller;

import com.example.demo.customer.entity.TcLibrary;
import com.example.demo.customer.entity.TcType;
import com.example.demo.customer.repository.TcLibraryRepository;
import com.example.demo.customer.repository.TcTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tc")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class TcLibraryController {

    @Autowired private TcLibraryRepository tcLibraryRepository;
    @Autowired private TcTypeRepository tcTypeRepository;

    // ── TYPES ──────────────────────────────────────────

    @GetMapping("/types")
    public List<TcType> getAllTypes() {
        return tcTypeRepository.findAll();
    }

    @PostMapping("/types")
    public ResponseEntity<TcType> createType(@RequestBody TcType tcType) {
        return new ResponseEntity<>(tcTypeRepository.save(tcType), HttpStatus.CREATED);
    }

    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        tcTypeRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ── TERMS ──────────────────────────────────────────

    @GetMapping("/terms")
    public List<TcLibrary> getAllTerms() {
        // Return sorted by sortOrder, falling back to termId for rows without a sortOrder yet
        return tcLibraryRepository.findAllByOrderBySortOrderAscTermIdAsc();
    }

    @PostMapping("/terms")
    public ResponseEntity<TcLibrary> createTerm(@RequestBody TcLibrary term) {
        if (term.getTcType() == null || term.getTcType().getTypeId() == null) {
            TcType generalType = tcTypeRepository.findByTypeName("General")
                    .orElseGet(() -> {
                        TcType t = new TcType();
                        t.setTypeName("General");
                        return tcTypeRepository.save(t);
                    });
            term.setTcType(generalType);
        } else {
            TcType type = tcTypeRepository.findById(term.getTcType().getTypeId())
                    .orElseThrow(() -> new RuntimeException("Type not found"));
            term.setTcType(type);
        }
        // If no sortOrder provided, put it at the end
        if (term.getSortOrder() == null) {
            int maxOrder = tcLibraryRepository.findAll().size();
            term.setSortOrder(maxOrder + 1);
        }
        return new ResponseEntity<>(tcLibraryRepository.save(term), HttpStatus.CREATED);
    }

    @PutMapping("/terms/{id}")
    public ResponseEntity<TcLibrary> updateTerm(@PathVariable Long id, @RequestBody TcLibrary term) {
        TcLibrary existing = tcLibraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        existing.setTermText(term.getTermText());
        if (term.getSortOrder() != null) {
            existing.setSortOrder(term.getSortOrder());
        }
        if (term.getTcType() != null && term.getTcType().getTypeId() != null) {
            TcType type = tcTypeRepository.findById(term.getTcType().getTypeId())
                    .orElseThrow(() -> new RuntimeException("Type not found"));
            existing.setTcType(type);
        } else {
            existing.setTcType(null);
        }
        return ResponseEntity.ok(tcLibraryRepository.save(existing));
    }

    @DeleteMapping("/terms/{id}")
    public ResponseEntity<Void> deleteTerm(@PathVariable Long id) {
        tcLibraryRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ── REORDER ────────────────────────────────────────

    // DTO for reorder request
    static class ReorderItem {
        public Long termId;
        public Integer sortOrder;
    }

    @PutMapping("/terms/reorder")
    public ResponseEntity<Void> reorderTerms(@RequestBody List<ReorderItem> items) {
        for (ReorderItem item : items) {
            tcLibraryRepository.findById(item.termId).ifPresent(term -> {
                term.setSortOrder(item.sortOrder);
                tcLibraryRepository.save(term);
            });
        }
        return ResponseEntity.ok().build();
    }
}
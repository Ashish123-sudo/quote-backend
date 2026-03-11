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
        return tcLibraryRepository.findAll();
    }

    @PostMapping("/terms")
    public ResponseEntity<TcLibrary> createTerm(@RequestBody TcLibrary term) {
        if (term.getTcType() != null && term.getTcType().getTypeId() != null) {
            TcType type = tcTypeRepository.findById(term.getTcType().getTypeId())
                    .orElseThrow(() -> new RuntimeException("Type not found"));
            term.setTcType(type);
        }
        return new ResponseEntity<>(tcLibraryRepository.save(term), HttpStatus.CREATED);
    }

    @PutMapping("/terms/{id}")
    public ResponseEntity<TcLibrary> updateTerm(@PathVariable Long id, @RequestBody TcLibrary term) {
        TcLibrary existing = tcLibraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        existing.setTermText(term.getTermText());
        if (term.getTcType() != null && term.getTcType().getTypeId() != null) {
            TcType type = tcTypeRepository.findById(term.getTcType().getTypeId())
                    .orElseThrow(() -> new RuntimeException("Type not found"));
            existing.setTcType(type);
        }
        return ResponseEntity.ok(tcLibraryRepository.save(existing));
    }

    @DeleteMapping("/terms/{id}")
    public ResponseEntity<Void> deleteTerm(@PathVariable Long id) {
        tcLibraryRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
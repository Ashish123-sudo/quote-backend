package com.example.demo.customer.controller;

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

@RestController
@RequestMapping("/api/tc/templates")
@CrossOrigin(origins = {
        "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app",
        "http://localhost:4200"
})
public class TcTemplateController {

    @Autowired private TcTemplateRepository tcTemplateRepository;
    @Autowired private TcLibraryRepository tcLibraryRepository;

    @GetMapping
    public List<TcTemplate> getAll() {
        return tcTemplateRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TcTemplate> getById(@PathVariable Long id) {
        return tcTemplateRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TcTemplate> create(@RequestBody Map<String, Object> payload) {
        TcTemplate template = new TcTemplate();
        template.setTemplateName((String) payload.get("templateName"));

        List<Integer> termIds = (List<Integer>) payload.get("termIds");
        if (termIds != null) {
            List<TcLibrary> terms = termIds.stream()
                    .map(id -> tcLibraryRepository.findById(Long.valueOf(id))
                            .orElseThrow(() -> new RuntimeException("Term not found: " + id)))
                    .toList();
            template.setTerms(terms);
        }

        return new ResponseEntity<>(tcTemplateRepository.save(template), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TcTemplate> update(@PathVariable Long id,
                                             @RequestBody Map<String, Object> payload) {
        TcTemplate template = tcTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        template.setTemplateName((String) payload.get("templateName"));

        List<Integer> termIds = (List<Integer>) payload.get("termIds");
        if (termIds != null) {
            List<TcLibrary> terms = termIds.stream()
                    .map(tid -> tcLibraryRepository.findById(Long.valueOf(tid))
                            .orElseThrow(() -> new RuntimeException("Term not found: " + tid)))
                    .toList();
            template.setTerms(terms);
        }

        return ResponseEntity.ok(tcTemplateRepository.save(template));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tcTemplateRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
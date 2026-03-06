package com.example.demo.customer.controller;

import com.example.demo.customer.entity.TermsTemplate;
import com.example.demo.customer.service.TermsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/terms")
@CrossOrigin(origins = "https://699db1e4064fec9991497b90--sprightly-vacherin-2274ac.netlify.app")
public class TermsController {

    @Autowired
    private TermsService termsService;

    // GET all templates
    @GetMapping
    public ResponseEntity<List<TermsTemplate>> getAllTemplates() {
        try {
            List<TermsTemplate> templates = termsService.getAllTemplates();
            return new ResponseEntity<>(templates, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET template by ID
    @GetMapping("/{id}")
    public ResponseEntity<TermsTemplate> getTemplateById(@PathVariable Long id) {
        Optional<TermsTemplate> template = termsService.getTemplateById(id);
        return template.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST create new template
    @PostMapping
    public ResponseEntity<TermsTemplate> createTemplate(@RequestBody TermsTemplate termsTemplate) {
        try {
            TermsTemplate created = termsService.createTemplate(termsTemplate);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update template
    @PutMapping("/{id}")
    public ResponseEntity<TermsTemplate> updateTemplate(@PathVariable Long id,
                                                        @RequestBody TermsTemplate termsTemplate) {
        try {
            TermsTemplate updated = termsService.updateTemplate(id, termsTemplate);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE template
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        try {
            termsService.deleteTemplate(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
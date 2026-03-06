package com.example.demo.customer.service;

import com.example.demo.customer.entity.TermsTemplate;

import java.util.List;
import java.util.Optional;

public interface TermsService {
    List<TermsTemplate> getAllTemplates();
    Optional<TermsTemplate> getTemplateById(Long id);
    TermsTemplate createTemplate(TermsTemplate termsTemplate);
    TermsTemplate updateTemplate(Long id, TermsTemplate termsTemplate);
    void deleteTemplate(Long id);
}
package com.example.demo.customer.service;

import com.example.demo.customer.entity.TermsDetail;
import com.example.demo.customer.entity.TermsGroup;
import com.example.demo.customer.entity.TermsTemplate;
import com.example.demo.customer.repository.TermsTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TermsServiceImpl implements TermsService {

    @Autowired
    private TermsTemplateRepository termsTemplateRepository;

    @Override
    public List<TermsTemplate> getAllTemplates() {
        return termsTemplateRepository.findAll();
    }

    @Override
    public Optional<TermsTemplate> getTemplateById(Long id) {
        return termsTemplateRepository.findById(id);
    }

    @Override
    public TermsTemplate createTemplate(TermsTemplate termsTemplate) {
        if (termsTemplate.getTermsGroups() != null) {
            for (int i = 0; i < termsTemplate.getTermsGroups().size(); i++) {
                TermsGroup group = termsTemplate.getTermsGroups().get(i);
                group.setTermsTemplate(termsTemplate);
                group.setSortOrder(i + 1);
                if (group.getTermsDetails() != null) {
                    for (int j = 0; j < group.getTermsDetails().size(); j++) {
                        TermsDetail detail = group.getTermsDetails().get(j);
                        detail.setTermsGroup(group);
                        detail.setSortOrder(j + 1);
                    }
                }
            }
        }
        return termsTemplateRepository.save(termsTemplate);
    }

    @Override
    public TermsTemplate updateTemplate(Long id, TermsTemplate termsTemplate) {
        TermsTemplate existing = termsTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terms template not found with id: " + id));

        // Update template name
        existing.setTemplateName(termsTemplate.getTemplateName());

        // Clear and replace groups (orphanRemoval will delete removed ones)
        existing.getTermsGroups().clear();

        if (termsTemplate.getTermsGroups() != null) {
            for (int i = 0; i < termsTemplate.getTermsGroups().size(); i++) {
                TermsGroup group = termsTemplate.getTermsGroups().get(i);
                group.setTermsTemplate(existing);
                group.setSortOrder(i + 1);

                if (group.getTermsDetails() != null) {
                    for (int j = 0; j < group.getTermsDetails().size(); j++) {
                        TermsDetail detail = group.getTermsDetails().get(j);
                        detail.setTermsGroup(group);
                        detail.setSortOrder(j + 1);
                    }
                }

                existing.getTermsGroups().add(group);
            }
        }

        return termsTemplateRepository.save(existing);
    }

    @Override
    public void deleteTemplate(Long id) {
        if (!termsTemplateRepository.existsById(id)) {
            throw new RuntimeException("Terms template not found with id: " + id);
        }
        termsTemplateRepository.deleteById(id);
    }
}
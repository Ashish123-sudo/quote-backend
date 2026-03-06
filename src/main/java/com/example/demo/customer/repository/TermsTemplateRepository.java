package com.example.demo.customer.repository;

import com.example.demo.customer.entity.TermsTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TermsTemplateRepository extends JpaRepository<TermsTemplate, Long> {
    Optional<TermsTemplate> findByTemplateName(String templateName);
}
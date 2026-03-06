package com.example.demo.customer.repository;

import com.example.demo.customer.entity.TermsGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermsGroupRepository extends JpaRepository<TermsGroup, Long> {
    List<TermsGroup> findByTermsTemplate_TemplateIdOrderBySortOrderAsc(Long templateId);
}
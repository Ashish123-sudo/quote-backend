package com.example.demo.customer.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "terms_template")
public class TermsTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "template_name", length = 255, nullable = false)
    private String templateName;

    @OneToMany(mappedBy = "termsTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TermsGroup> termsGroups = new ArrayList<>();

    public TermsTemplate() {}

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public List<TermsGroup> getTermsGroups() { return termsGroups; }
    public void setTermsGroups(List<TermsGroup> termsGroups) { this.termsGroups = termsGroups; }
}
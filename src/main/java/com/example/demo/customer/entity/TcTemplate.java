package com.example.demo.customer.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tc_template")
public class TcTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "template_name", length = 200, nullable = false)
    private String templateName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tc_template_item",
            joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "term_id")
    )
    private List<TcLibrary> terms;

    public TcTemplate() {}

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public List<TcLibrary> getTerms() { return terms; }
    public void setTerms(List<TcLibrary> terms) { this.terms = terms; }
}
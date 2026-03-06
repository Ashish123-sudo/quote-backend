package com.example.demo.customer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "terms_detail")
public class TermsDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long detailId;

    @Column(name = "term_text", columnDefinition = "TEXT", nullable = false)
    private String termText;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @JsonIgnore
    private TermsGroup termsGroup;

    public TermsDetail() {}

    public Long getDetailId() { return detailId; }
    public void setDetailId(Long detailId) { this.detailId = detailId; }

    public String getTermText() { return termText; }
    public void setTermText(String termText) { this.termText = termText; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public TermsGroup getTermsGroup() { return termsGroup; }
    public void setTermsGroup(TermsGroup termsGroup) { this.termsGroup = termsGroup; }
}
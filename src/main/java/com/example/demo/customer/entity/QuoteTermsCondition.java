package com.example.demo.quote.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "quote_terms_conditions")
public class QuoteTermsCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "org_id", nullable = false, columnDefinition = "UUID")
    private UUID orgId;

    @Column(name = "quote_ref", length = 50)
    private String quoteRef;

    @Column(name = "group_name", length = 255)
    private String groupName;

    @Column(name = "term_text", columnDefinition = "TEXT")
    private String termText;

    @Column(name = "group_order")
    private Integer groupOrder;

    @Column(name = "term_order")
    private Integer termOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    @JsonIgnore
    private QuoteHeader quoteHeader;

    // Audit fields
    @Column(name = "created_by", columnDefinition = "UUID")
    private UUID createdBy;

    @Column(name = "created_datetime", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDatetime;

    @Column(name = "updated_by", columnDefinition = "UUID")
    private UUID updatedBy;

    @Column(name = "updated_datetime")
    @UpdateTimestamp
    private LocalDateTime updatedDatetime;

    // Constructors
    public QuoteTermsCondition() {
    }

    public QuoteTermsCondition(UUID orgId, String quoteRef, String groupName, String termText,
                               Integer groupOrder, Integer termOrder) {
        this.orgId = orgId;
        this.quoteRef = quoteRef;
        this.groupName = groupName;
        this.termText = termText;
        this.groupOrder = groupOrder;
        this.termOrder = termOrder;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrgId() {
        return orgId;
    }

    public void setOrgId(UUID orgId) {
        this.orgId = orgId;
    }

    public String getQuoteRef() {
        return quoteRef;
    }

    public void setQuoteRef(String quoteRef) {
        this.quoteRef = quoteRef;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTermText() {
        return termText;
    }

    public void setTermText(String termText) {
        this.termText = termText;
    }

    public Integer getGroupOrder() {
        return groupOrder;
    }

    public void setGroupOrder(Integer groupOrder) {
        this.groupOrder = groupOrder;
    }

    public Integer getTermOrder() {
        return termOrder;
    }

    public void setTermOrder(Integer termOrder) {
        this.termOrder = termOrder;
    }

    public QuoteHeader getQuoteHeader() {
        return quoteHeader;
    }

    public void setQuoteHeader(QuoteHeader quoteHeader) {
        this.quoteHeader = quoteHeader;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(LocalDateTime updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    @Override
    public String toString() {
        return "QuoteTermsCondition{" +
                "id=" + id +
                ", orgId=" + orgId +
                ", quoteRef='" + quoteRef + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupOrder=" + groupOrder +
                ", termOrder=" + termOrder +
                '}';
    }
}
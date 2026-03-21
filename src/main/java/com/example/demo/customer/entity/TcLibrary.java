package com.example.demo.customer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tc_library")
public class TcLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "term_id", columnDefinition = "UUID")
    private UUID termId;

    @Column(name = "org_id", nullable = false, columnDefinition = "UUID")
    private UUID orgId;

    @Column(name = "term_text", columnDefinition = "TEXT", nullable = false)
    private String termText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TcType tcType;

    @Column(name = "sort_order")
    private Integer sortOrder;

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
    public TcLibrary() {
    }

    public TcLibrary(UUID orgId, String termText, TcType tcType, Integer sortOrder) {
        this.orgId = orgId;
        this.termText = termText;
        this.tcType = tcType;
        this.sortOrder = sortOrder;
    }

    // Getters and Setters
    public UUID getTermId() {
        return termId;
    }

    public void setTermId(UUID termId) {
        this.termId = termId;
    }

    public UUID getOrgId() {
        return orgId;
    }

    public void setOrgId(UUID orgId) {
        this.orgId = orgId;
    }

    public String getTermText() {
        return termText;
    }

    public void setTermText(String termText) {
        this.termText = termText;
    }

    public TcType getTcType() {
        return tcType;
    }

    public void setTcType(TcType tcType) {
        this.tcType = tcType;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
        return "TcLibrary{" +
                "termId=" + termId +
                ", orgId=" + orgId +
                ", termText='" + termText + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
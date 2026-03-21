package com.example.demo.quote.entity;

import com.example.demo.customer.entity.Customer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "quote_header")
public class QuoteHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "quote_id", columnDefinition = "UUID")
    private UUID quoteId;

    @Column(name = "org_id", nullable = false, columnDefinition = "UUID")
    private UUID orgId;

    @Column(name = "quote_ref", length = 50, nullable = false)
    private String quoteRef;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "quotes"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "quote_date", nullable = false)
    private LocalDate quoteDate;

    @Column(name = "currency", length = 10)
    private String currency = "INR";

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "DRAFT";

    @Column(name = "submitted_by", length = 100)
    private String submittedBy;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "total_quantity")
    private Integer totalQuantity = 0;

    @Column(name = "total_value", precision = 15, scale = 2)
    private BigDecimal totalValue;

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

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "quoteHeader"})
    @OneToMany(mappedBy = "quoteHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuoteDetail> quoteDetails = new ArrayList<>();

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "quoteHeader"})
    @OneToMany(mappedBy = "quoteHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuoteTermsCondition> quoteTermsConditions = new HashSet<>();  // ✅ Set not List

    @Transient
    @JsonProperty("incomingTerms")
    private List<QuoteTermsCondition> incomingTerms;

    public QuoteHeader() {}

    public QuoteHeader(UUID orgId, String quoteRef, Customer customer, LocalDate quoteDate,
                       Integer totalQuantity, BigDecimal totalValue) {
        this.orgId = orgId;
        this.quoteRef = quoteRef;
        this.customer = customer;
        this.quoteDate = quoteDate;
        this.totalQuantity = totalQuantity;
        this.totalValue = totalValue;
    }

    public UUID getQuoteId() { return quoteId; }
    public void setQuoteId(UUID quoteId) { this.quoteId = quoteId; }

    public UUID getOrgId() { return orgId; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }

    public String getQuoteRef() { return quoteRef; }
    public void setQuoteRef(String quoteRef) { this.quoteRef = quoteRef; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public UUID getCustomerId() {
        return customer != null ? customer.getCustomerId() : null;
    }
    public void setCustomerId(UUID customerId) {}

    public LocalDate getQuoteDate() { return quoteDate; }
    public void setQuoteDate(LocalDate quoteDate) { this.quoteDate = quoteDate; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDatetime() { return createdDatetime; }
    public void setCreatedDatetime(LocalDateTime createdDatetime) { this.createdDatetime = createdDatetime; }

    public UUID getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDatetime() { return updatedDatetime; }
    public void setUpdatedDatetime(LocalDateTime updatedDatetime) { this.updatedDatetime = updatedDatetime; }

    public List<QuoteDetail> getQuoteDetails() { return quoteDetails; }
    public void setQuoteDetails(List<QuoteDetail> quoteDetails) { this.quoteDetails = quoteDetails; }

    public Set<QuoteTermsCondition> getQuoteTermsConditions() { return quoteTermsConditions; }
    public void setQuoteTermsConditions(Set<QuoteTermsCondition> quoteTermsConditions) { this.quoteTermsConditions = quoteTermsConditions; }

    public List<QuoteTermsCondition> getIncomingTerms() { return incomingTerms; }
    public void setIncomingTerms(List<QuoteTermsCondition> incomingTerms) { this.incomingTerms = incomingTerms; }

    public void addQuoteDetail(QuoteDetail detail) {
        quoteDetails.add(detail);
        detail.setQuoteHeader(this);
    }

    public void removeQuoteDetail(QuoteDetail detail) {
        quoteDetails.remove(detail);
        detail.setQuoteHeader(null);
    }

    @Override
    public String toString() {
        return "QuoteHeader{quoteId=" + quoteId + ", quoteRef='" + quoteRef + "', approvalStatus='" + approvalStatus + "'}";
    }
}
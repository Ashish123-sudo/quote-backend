package com.example.demo.quote.entity;

import com.example.demo.customer.entity.Customer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "quote_header")
public class QuoteHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "quote_id")                         // ✅ removed columnDefinition = "UUID"
    private UUID quoteId;

    @Column(name = "org_id", nullable = false)         // ✅ removed columnDefinition = "UUID"
    private UUID orgId;

    @Column(name = "quote_ref", length = 50, nullable = false)
    private String quoteRef;

    @ManyToOne(fetch = FetchType.LAZY)                 // ✅ changed EAGER → LAZY
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "quotes"})
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

    // Audit fields
    @Column(name = "created_by")                       // ✅ removed columnDefinition = "UUID"
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_datetime", updatable = false)
    private LocalDateTime createdDatetime;

    @Column(name = "updated_by")                       // ✅ removed columnDefinition = "UUID"
    private UUID updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime;

    @OneToMany(mappedBy = "quoteHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "quoteHeader"})
    private List<QuoteDetail> quoteDetails = new ArrayList<>(); // ✅ changed EAGER → LAZY (default)

    @OneToMany(mappedBy = "quoteHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "quoteHeader"})
    private Set<QuoteTermsCondition> quoteTermsConditions = new HashSet<>(); // ✅ changed EAGER → LAZY (default)

    // ✅ kept @Transient for incoming terms from frontend
    @Transient
    @JsonProperty("incomingTerms")
    private List<QuoteTermsCondition> incomingTerms;

    public QuoteHeader(UUID orgId, String quoteRef, Customer customer, LocalDate quoteDate,
                       Integer totalQuantity, BigDecimal totalValue) {
        this.orgId = orgId;
        this.quoteRef = quoteRef;
        this.customer = customer;
        this.quoteDate = quoteDate;
        this.totalQuantity = totalQuantity;
        this.totalValue = totalValue;
    }

    // ✅ derived getter — safe, reads from loaded customer
    public UUID getCustomerId() {
        return customer != null ? customer.getCustomerId() : null;
    }

    // ✅ removed empty setCustomerId() — was a silent no-op bug
    // Handle customerId → customer resolution in the service layer instead

    // ✅ kept helper methods
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
        return "QuoteHeader{quoteId=" + quoteId +
                ", quoteRef='" + quoteRef + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                '}';
    }
}
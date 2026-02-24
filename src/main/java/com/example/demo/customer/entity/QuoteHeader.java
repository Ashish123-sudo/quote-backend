package com.example.demo.quote.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quote_header")
public class QuoteHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quote_id")
    private Long quoteId;

    @Column(name = "quote_ref", length = 50)
    private String quoteRef;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "quote_date")
    private LocalDate quoteDate;

    @Column(name = "total_quantity")
    private Integer totalQuantity = 0;

    @Column(name = "total_value")
    private Double totalValue;

    @OneToMany(mappedBy = "quoteHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuoteDetail> quoteDetails = new ArrayList<>();

    // Constructors
    public QuoteHeader() {
    }

    public QuoteHeader(String quoteRef, Integer customerId, LocalDate quoteDate,
                       Integer totalQuantity, Double totalValue) {
        this.quoteRef = quoteRef;
        this.customerId = customerId;
        this.quoteDate = quoteDate;
        this.totalQuantity = totalQuantity;
        this.totalValue = totalValue;
    }

    // Getters and Setters
    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuoteRef() {
        return quoteRef;
    }

    public void setQuoteRef(String quoteRef) {
        this.quoteRef = quoteRef;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public LocalDate getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(LocalDate quoteDate) {
        this.quoteDate = quoteDate;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public List<QuoteDetail> getQuoteDetails() {
        return quoteDetails;
    }

    public void setQuoteDetails(List<QuoteDetail> quoteDetails) {
        this.quoteDetails = quoteDetails;
    }

    // Helper methods
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
        return "QuoteHeader{" +
                "quoteId=" + quoteId +
                ", quoteRef='" + quoteRef + '\'' +
                ", customerId=" + customerId +
                ", quoteDate=" + quoteDate +
                ", totalValue=" + totalValue +
                '}';
    }
}
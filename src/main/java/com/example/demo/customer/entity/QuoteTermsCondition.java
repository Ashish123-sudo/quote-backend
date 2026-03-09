package com.example.demo.quote.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "quote_terms_conditions")
public class QuoteTermsCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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

    public QuoteTermsCondition() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuoteRef() { return quoteRef; }
    public void setQuoteRef(String quoteRef) { this.quoteRef = quoteRef; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getTermText() { return termText; }
    public void setTermText(String termText) { this.termText = termText; }

    public Integer getGroupOrder() { return groupOrder; }
    public void setGroupOrder(Integer groupOrder) { this.groupOrder = groupOrder; }

    public Integer getTermOrder() { return termOrder; }
    public void setTermOrder(Integer termOrder) { this.termOrder = termOrder; }

    public QuoteHeader getQuoteHeader() { return quoteHeader; }
    public void setQuoteHeader(QuoteHeader quoteHeader) { this.quoteHeader = quoteHeader; }
}
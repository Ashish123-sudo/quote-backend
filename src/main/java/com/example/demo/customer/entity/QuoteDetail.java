package com.example.demo.quote.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "quote_details")
public class QuoteDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sl_no")
    private Long slNo;

    @Column(name = "quote_ref", length = 50)
    private String quoteRef;

    @Column(name = "item_desc", length = 255)
    private String itemDesc;

    @Column(name = "item_unit_rate")
    private Double itemUnitRate;

    @Column(name = "item_quantity")
    private Integer itemQuantity;

    @Column(name = "item_value")
    private Double itemValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    @JsonIgnore
    private QuoteHeader quoteHeader;

    // Transient field for JSON deserialization (not stored in database)
    @Transient
    private Long quoteId;

    // Constructors
    public QuoteDetail() {
    }

    public QuoteDetail(String quoteRef, String itemDesc, Double itemUnitRate,
                       Integer itemQuantity, Double itemValue) {
        this.quoteRef = quoteRef;
        this.itemDesc = itemDesc;
        this.itemUnitRate = itemUnitRate;
        this.itemQuantity = itemQuantity;
        this.itemValue = itemValue;
    }

    // Getters and Setters
    public Long getSlNo() {
        return slNo;
    }

    public void setSlNo(Long slNo) {
        this.slNo = slNo;
    }

    public String getQuoteRef() {
        return quoteRef;
    }

    public void setQuoteRef(String quoteRef) {
        this.quoteRef = quoteRef;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public Double getItemUnitRate() {
        return itemUnitRate;
    }

    public void setItemUnitRate(Double itemUnitRate) {
        this.itemUnitRate = itemUnitRate;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public Double getItemValue() {
        return itemValue;
    }

    public void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    public QuoteHeader getQuoteHeader() {
        return quoteHeader;
    }

    public void setQuoteHeader(QuoteHeader quoteHeader) {
        this.quoteHeader = quoteHeader;
    }

    // Transient quoteId getter and setter (for JSON binding)
    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    @Override
    public String toString() {
        return "QuoteDetail{" +
                "slNo=" + slNo +
                ", quoteRef='" + quoteRef + '\'' +
                ", itemDesc='" + itemDesc + '\'' +
                ", itemQuantity=" + itemQuantity +
                ", itemValue=" + itemValue +
                '}';
    }
}
package com.example.demo.quote.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "quote_details")
public class QuoteDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sl_no", columnDefinition = "UUID")
    private UUID slNo;

    @Column(name = "org_id", nullable = false, columnDefinition = "UUID")
    private UUID orgId;

    @Column(name = "quote_ref", length = 50, nullable = false)
    private String quoteRef;

    @Column(name = "item_desc", length = 255)
    private String itemDesc;

    @Column(name = "item_unit_rate", precision = 15, scale = 2)
    private BigDecimal itemUnitRate;

    @Column(name = "item_quantity", nullable = false)
    private Integer itemQuantity;

    @Column(name = "item_discount", precision = 15, scale = 2)
    private BigDecimal itemDiscount = BigDecimal.ZERO;

    @Column(name = "item_value", precision = 15, scale = 2)
    private BigDecimal itemValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
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

    // Transient field for JSON deserialization (not stored in database)
    @Transient
    private UUID quoteId;

    // Constructors
    public QuoteDetail() {
    }

    public QuoteDetail(UUID orgId, String quoteRef, String itemDesc, BigDecimal itemUnitRate,
                       Integer itemQuantity, BigDecimal itemValue) {
        this.orgId = orgId;
        this.quoteRef = quoteRef;
        this.itemDesc = itemDesc;
        this.itemUnitRate = itemUnitRate;
        this.itemQuantity = itemQuantity;
        this.itemValue = itemValue;
    }

    // Getters and Setters
    public UUID getSlNo() {
        return slNo;
    }

    public void setSlNo(UUID slNo) {
        this.slNo = slNo;
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

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public BigDecimal getItemUnitRate() {
        return itemUnitRate;
    }

    public void setItemUnitRate(BigDecimal itemUnitRate) {
        this.itemUnitRate = itemUnitRate;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public BigDecimal getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(BigDecimal itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public BigDecimal getItemValue() {
        return itemValue;
    }

    public void setItemValue(BigDecimal itemValue) {
        this.itemValue = itemValue;
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

    // Transient quoteId getter and setter (for JSON binding)
    public UUID getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(UUID quoteId) {
        this.quoteId = quoteId;
    }

    @Override
    public String toString() {
        return "QuoteDetail{" +
                "slNo=" + slNo +
                ", orgId=" + orgId +
                ", quoteRef='" + quoteRef + '\'' +
                ", itemDesc='" + itemDesc + '\'' +
                ", itemQuantity=" + itemQuantity +
                ", itemValue=" + itemValue +
                '}';
    }
}
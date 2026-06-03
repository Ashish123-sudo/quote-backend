package com.example.demo.quote.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "quote_details")
public class QuoteDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sl_no")                            // ✅ removed columnDefinition = "UUID"
    private UUID slNo;

    @Column(name = "org_id", nullable = false)         // ✅ removed columnDefinition = "UUID"
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

    @ManyToOne(fetch = FetchType.LAZY)                 // ✅ already LAZY — correct
    @JoinColumn(name = "quote_id", nullable = false)
    @JsonIgnore
    private QuoteHeader quoteHeader;

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

    // ✅ kept @Transient for JSON deserialization — not stored in DB
    @Transient
    private UUID quoteId;

    public QuoteDetail(UUID orgId, String quoteRef, String itemDesc, BigDecimal itemUnitRate,
                       Integer itemQuantity, BigDecimal itemValue) {
        this.orgId = orgId;
        this.quoteRef = quoteRef;
        this.itemDesc = itemDesc;
        this.itemUnitRate = itemUnitRate;
        this.itemQuantity = itemQuantity;
        this.itemValue = itemValue;
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
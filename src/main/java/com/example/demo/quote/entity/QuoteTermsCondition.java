package com.example.demo.quote.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "quote_terms_conditions")
public class QuoteTermsCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")                               // ✅ removed columnDefinition = "UUID"
    private UUID id;

    @Column(name = "org_id", nullable = false)         // ✅ removed columnDefinition = "UUID"
    private UUID orgId;

    @Column(name = "quote_ref", length = 50)
    private String quoteRef;

    @Column(name = "group_name", length = 255)
    private String groupName;

    @Column(name = "term_text", columnDefinition = "TEXT") // ✅ kept — TEXT is content type not UUID
    private String termText;

    @Column(name = "group_order")
    private Integer groupOrder;

    @Column(name = "term_order")
    private Integer termOrder;

    @ManyToOne(fetch = FetchType.LAZY)                 // ✅ already LAZY — correct
    @JoinColumn(name = "quote_id")
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

    public QuoteTermsCondition(UUID orgId, String quoteRef, String groupName, String termText,
                               Integer groupOrder, Integer termOrder) {
        this.orgId = orgId;
        this.quoteRef = quoteRef;
        this.groupName = groupName;
        this.termText = termText;
        this.groupOrder = groupOrder;
        this.termOrder = termOrder;
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
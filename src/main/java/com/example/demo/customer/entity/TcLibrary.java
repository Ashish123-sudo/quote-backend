package com.example.demo.customer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "tc_library")
public class TcLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "term_id")                          // ✅ removed columnDefinition = "UUID"
    private UUID termId;

    @Column(name = "org_id", nullable = false)         // ✅ removed columnDefinition = "UUID"
    private UUID orgId;

    @Column(name = "term_text", columnDefinition = "TEXT", nullable = false)
    private String termText;                           // ✅ kept TEXT — this is content type, not UUID

    @ManyToOne(fetch = FetchType.LAZY)                 // ✅ changed EAGER → LAZY
    @JoinColumn(name = "type_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TcType tcType;

    @Column(name = "sort_order")
    private Integer sortOrder;

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

    public TcLibrary(UUID orgId, String termText, TcType tcType, Integer sortOrder) {
        this.orgId = orgId;
        this.termText = termText;
        this.tcType = tcType;
        this.sortOrder = sortOrder;
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
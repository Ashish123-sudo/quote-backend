package com.example.demo.customer.entity;

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
@Table(name = "currency")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "currency_id")                      // ✅ removed columnDefinition = "UUID"
    private UUID currencyId;

    @Column(name = "org_id", nullable = false)         // ✅ removed columnDefinition = "UUID"
    private UUID orgId;

    @Column(name = "currency_code", length = 10, nullable = false)
    private String currencyCode;

    @Column(name = "currency_name", length = 100)
    private String currencyName;

    @Column(name = "is_default")
    private Boolean isDefault = false;

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

    public Currency(UUID orgId, String currencyCode, String currencyName, Boolean isDefault) {
        this.orgId = orgId;
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "currencyId=" + currencyId +
                ", orgId=" + orgId +
                ", currencyCode='" + currencyCode + '\'' +
                ", currencyName='" + currencyName + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
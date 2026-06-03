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
@Table(name = "tc_type")
public class TcType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "type_id")                          // ✅ removed columnDefinition = "UUID"
    private UUID typeId;

    @Column(name = "org_id", nullable = false)         // ✅ removed columnDefinition = "UUID"
    private UUID orgId;

    @Column(name = "type_name", length = 100, nullable = false)
    private String typeName;

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

    public TcType(UUID orgId, String typeName) {
        this.orgId = orgId;
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "TcType{" +
                "typeId=" + typeId +
                ", orgId=" + orgId +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
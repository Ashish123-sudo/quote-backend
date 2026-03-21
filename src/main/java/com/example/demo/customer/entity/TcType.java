package com.example.demo.customer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tc_type")
public class TcType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "type_id", columnDefinition = "UUID")
    private UUID typeId;

    @Column(name = "org_id", nullable = false, columnDefinition = "UUID")
    private UUID orgId;

    @Column(name = "type_name", length = 100, nullable = false)
    private String typeName;

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

    // Constructors
    public TcType() {
    }

    public TcType(UUID orgId, String typeName) {
        this.orgId = orgId;
        this.typeName = typeName;
    }

    // Getters and Setters
    public UUID getTypeId() {
        return typeId;
    }

    public void setTypeId(UUID typeId) {
        this.typeId = typeId;
    }

    public UUID getOrgId() {
        return orgId;
    }

    public void setOrgId(UUID orgId) {
        this.orgId = orgId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    @Override
    public String toString() {
        return "TcType{" +
                "typeId=" + typeId +
                ", orgId=" + orgId +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
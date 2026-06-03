package com.example.demo.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "app_role")
public class AppRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id")                          // ✅ removed columnDefinition = "UUID"
    private UUID roleId;

    @Column(name = "org_id", nullable = false)         // ✅ removed columnDefinition = "UUID"
    private UUID orgId;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "description")
    private String description;

    // Audit fields
    @Column(name = "created_by")                       // ✅ removed columnDefinition = "UUID"
    private UUID createdBy;

    @CreationTimestamp                                  // ✅ moved above @Column (cleaner order)
    @Column(name = "created_datetime", updatable = false)
    private LocalDateTime createdDatetime;

    @Column(name = "updated_by")                       // ✅ removed columnDefinition = "UUID"
    private UUID updatedBy;

    @UpdateTimestamp                                    // ✅ moved above @Column
    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime;

    // Kept this constructor since Lombok only generates no-arg
    public AppRole(UUID orgId, String roleName, String description) {
        this.orgId = orgId;
        this.roleName = roleName;
        this.description = description;
    }

    @Override
    public String toString() {
        return "AppRole{" +
                "roleId=" + roleId +
                ", orgId=" + orgId +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
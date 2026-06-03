package com.example.demo.organization.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "organization")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "org_id")                           // ✅ removed columnDefinition = "UUID"
    private UUID orgId;

    @Column(name = "org_name", length = 255, nullable = false)
    private String orgName;

    @Column(name = "org_code", length = 50, nullable = false, unique = true)
    private String orgCode;

    // Address
    @Column(name = "address1", length = 255)
    private String address1;

    @Column(name = "address2", length = 255)
    private String address2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state_province", length = 100)
    private String stateProvince;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    // Contact info
    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "fax", length = 50)
    private String fax;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "website", length = 255)
    private String website;

    // Business info
    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "company_size", length = 50)
    private String companySize;

    @Column(name = "tax_id", length = 100)
    private String taxId;

    // Subscription
    @Column(name = "subscription_tier", length = 50)
    private String subscriptionTier;

    @Column(name = "subscription_start_date")
    private LocalDate subscriptionStartDate;

    @Column(name = "subscription_end_date")
    private LocalDate subscriptionEndDate;

    // Status
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "notes", columnDefinition = "TEXT") // ✅ kept — TEXT is content type not UUID
    private String notes;

    // ✅ changed EAGER → LAZY
    @JsonIgnore
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrganizationContact> contacts = new ArrayList<>();

    // Audit fields
    // ✅ Correct audit block
    @Column(name = "created_by")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_datetime", updatable = false)  // ✅ correct column
    private LocalDateTime createdDatetime;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime;

    public Organization(String orgName, String orgCode) {
        this.orgName = orgName;
        this.orgCode = orgCode;
    }

    // ✅ helper — add contact and set back-reference
    public void addContact(OrganizationContact contact) {
        contacts.add(contact);
        contact.setOrganization(this);
    }

    // ✅ helper — remove contact and clear back-reference
    public void removeContact(OrganizationContact contact) {
        contacts.remove(contact);
        contact.setOrganization(null);
    }

    @Override
    public String toString() {
        return "Organization{orgId=" + orgId +
                ", orgCode='" + orgCode + '\'' +
                ", orgName='" + orgName + '\'' +
                '}';
    }
}
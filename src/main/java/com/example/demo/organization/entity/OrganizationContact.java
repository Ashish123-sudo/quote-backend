package com.example.demo.organization.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "organization_contact")
public class OrganizationContact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "contact_id")                       // ✅ removed columnDefinition = "UUID"
    private UUID contactId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    // Contact details
    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Column(name = "contact_mobile", length = 50)
    private String contactMobile;

    // Name breakdown
    @Column(name = "salutation", length = 20)
    private String salutation;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    // Role info
    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "department", length = 100)
    private String department;

    // Office contact details
    @Column(name = "office_desk_number", length = 50)
    private String officeDeskNumber;

    @Column(name = "office_extension", length = 20)
    private String officeExtension;

    @Column(name = "alternate_mobile", length = 50)
    private String alternateMobile;

    @Column(name = "fax_number", length = 50)
    private String faxNumber;

    @Column(name = "linkedin_profile", length = 255)
    private String linkedinProfile;

    // Flags
    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

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

    // ✅ kept derived getter — reads from loaded organization
    public UUID getOrgId() {
        return organization != null ? organization.getOrgId() : null;
    }

    @Override
    public String toString() {
        return "OrganizationContact{contactId=" + contactId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
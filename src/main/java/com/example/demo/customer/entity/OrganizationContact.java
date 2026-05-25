package com.example.demo.organization.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organization_contact")
public class OrganizationContact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "contact_id", columnDefinition = "UUID")
    private UUID contactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "contacts"})
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

    // Audit
    @Column(name = "created_by", columnDefinition = "UUID")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_datetime", updatable = false)
    private LocalDateTime createdDatetime;

    @Column(name = "updated_by", columnDefinition = "UUID")
    private UUID updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime;

    // Constructors
    public OrganizationContact() {}

    // Getters and Setters
    public UUID getContactId() { return contactId; }
    public void setContactId(UUID contactId) { this.contactId = contactId; }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }

    public UUID getOrgId() {
        return organization != null ? organization.getOrgId() : null;
    }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactMobile() { return contactMobile; }
    public void setContactMobile(String contactMobile) { this.contactMobile = contactMobile; }

    public String getSalutation() { return salutation; }
    public void setSalutation(String salutation) { this.salutation = salutation; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getOfficeDeskNumber() { return officeDeskNumber; }
    public void setOfficeDeskNumber(String officeDeskNumber) { this.officeDeskNumber = officeDeskNumber; }

    public String getOfficeExtension() { return officeExtension; }
    public void setOfficeExtension(String officeExtension) { this.officeExtension = officeExtension; }

    public String getAlternateMobile() { return alternateMobile; }
    public void setAlternateMobile(String alternateMobile) { this.alternateMobile = alternateMobile; }

    public String getFaxNumber() { return faxNumber; }
    public void setFaxNumber(String faxNumber) { this.faxNumber = faxNumber; }

    public String getLinkedinProfile() { return linkedinProfile; }
    public void setLinkedinProfile(String linkedinProfile) { this.linkedinProfile = linkedinProfile; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDatetime() { return createdDatetime; }
    public void setCreatedDatetime(LocalDateTime createdDatetime) { this.createdDatetime = createdDatetime; }

    public UUID getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedDatetime() { return updatedDatetime; }
    public void setUpdatedDatetime(LocalDateTime updatedDatetime) { this.updatedDatetime = updatedDatetime; }

    @Override
    public String toString() {
        return "OrganizationContact{contactId=" + contactId + ", firstName='" + firstName + "', lastName='" + lastName + "'}";
    }
}
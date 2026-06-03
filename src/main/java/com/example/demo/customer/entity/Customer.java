package com.example.demo.customer.entity;

import com.example.demo.quote.entity.QuoteHeader;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customer_details")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_id")                      // ✅ removed columnDefinition = "UUID"
    private UUID customerId;

    @Column(name = "org_id", nullable = false)         // ✅ removed columnDefinition = "UUID"
    private UUID orgId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address1", length = 255)
    private String address1;

    @Column(name = "address2", length = 255)
    private String address2;

    @Column(name = "city", length = 255)
    private String city;

    @Column(name = "state_province", length = 255)
    private String stateProvince;

    @Column(name = "country", length = 255)
    private String country;

    @Column(name = "contact_number", length = 255)
    private String contactNumber;

    @Column(name = "email_id", length = 255)
    private String emailId;

    @Column(name = "web_url", length = 255)
    private String webUrl;

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

    // ✅ changed EAGER → LAZY, @JsonIgnore kept
    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuoteHeader> quotes = new ArrayList<>();

    public Customer(UUID orgId, String name, String address1, String address2,
                    String city, String stateProvince, String country,
                    String contactNumber, String emailId, String webUrl) {
        this.orgId = orgId;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.stateProvince = stateProvince;
        this.country = country;
        this.contactNumber = contactNumber;
        this.emailId = emailId;
        this.webUrl = webUrl;
    }

    // ✅ helper — add quote and set back-reference
    public void addQuote(QuoteHeader quote) {
        quotes.add(quote);
        quote.setCustomer(this);
    }

    // ✅ helper — remove quote and clear back-reference
    public void removeQuote(QuoteHeader quote) {
        quotes.remove(quote);
        quote.setCustomer(null);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", orgId=" + orgId +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
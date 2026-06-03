package com.example.demo.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "quote_ref_config")
public class QuoteRefConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "config_id")                        // ✅ removed columnDefinition = "UUID"
    private UUID configId;

    @Column(name = "org_id", nullable = false, unique = true) // ✅ removed columnDefinition = "UUID"
    private UUID orgId;

    @Column(name = "prefix", length = 20, nullable = false)
    private String prefix = "Q";

    @Column(name = "separator", length = 5, nullable = false)
    private String separator = "-";

    @Column(name = "date_format", length = 20, nullable = false)
    private String dateFormat = "ddMMyy";

    @Column(name = "serial_digits", nullable = false)
    private Integer serialDigits = 3;

    @Column(name = "reset_type", length = 20, nullable = false)
    private String resetType = "NEVER";

    @Column(name = "reset_at_value")
    private Integer resetAtValue;

    @Column(name = "suffix", length = 20)
    private String suffix;

    @Column(name = "last_serial", nullable = false)
    private Integer lastSerial = 0;

    @Column(name = "last_reset_date")
    private LocalDate lastResetDate;

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
}
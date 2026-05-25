package com.example.demo.customer.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "quote_ref_config")
public class QuoteRefConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "config_id", columnDefinition = "UUID")
    private UUID configId;

    @Column(name = "org_id", nullable = false, unique = true, columnDefinition = "UUID")
    private UUID orgId;

    @Column(name = "prefix", length = 20, nullable = false)
    private String prefix = "Q";

    @Column(name = "separator", length = 5, nullable = false)
    private String separator = "-";

    @Column(name = "date_format", length = 20, nullable = false)
    private String dateFormat = "ddMMyy"; // ddMMyy | MMMyyy | MMMyyyy | yyyy | NONE

    @Column(name = "serial_digits", nullable = false)
    private Integer serialDigits = 3;

    @Column(name = "reset_type", length = 20, nullable = false)
    private String resetType = "NEVER"; // MONTHLY | YEARLY | AT_VALUE

    @Column(name = "reset_at_value")
    private Integer resetAtValue;

    @Column(name = "suffix", length = 20)
    private String suffix;

    @Column(name = "last_serial", nullable = false)
    private Integer lastSerial = 0;

    @Column(name = "last_reset_date")
    private LocalDate lastResetDate;

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

    // Getters and Setters
    public UUID getConfigId() { return configId; }
    public void setConfigId(UUID configId) { this.configId = configId; }
    public UUID getOrgId() { return orgId; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public String getSeparator() { return separator; }
    public void setSeparator(String separator) { this.separator = separator; }
    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
    public Integer getSerialDigits() { return serialDigits; }
    public void setSerialDigits(Integer serialDigits) { this.serialDigits = serialDigits; }
    public String getResetType() { return resetType; }
    public void setResetType(String resetType) { this.resetType = resetType; }
    public Integer getResetAtValue() { return resetAtValue; }
    public void setResetAtValue(Integer resetAtValue) { this.resetAtValue = resetAtValue; }
    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }
    public Integer getLastSerial() { return lastSerial; }
    public void setLastSerial(Integer lastSerial) { this.lastSerial = lastSerial; }
    public LocalDate getLastResetDate() { return lastResetDate; }
    public void setLastResetDate(LocalDate lastResetDate) { this.lastResetDate = lastResetDate; }
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    public UUID getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }
}
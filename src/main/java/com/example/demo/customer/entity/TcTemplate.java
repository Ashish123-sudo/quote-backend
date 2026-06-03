package com.example.demo.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tc_template")
public class TcTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "template_id")                      // ✅ removed columnDefinition = "UUID"
    private UUID templateId;

    @Column(name = "org_id", nullable = false)         // ✅ removed columnDefinition = "UUID"
    private UUID orgId;

    @Column(name = "template_name", length = 200, nullable = false)
    private String templateName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // ✅ replaced @ManyToMany + @JoinTable with proper join entity
    @JsonIgnore
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TcTemplateItem> templateItems = new ArrayList<>();

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

    public TcTemplate(UUID orgId, String templateName) {
        this.orgId = orgId;
        this.templateName = templateName;
    }

    // ✅ helper — get flat list of TcLibrary terms without exposing join entity to callers
    public List<TcLibrary> getTerms() {
        return templateItems.stream()
                .map(TcTemplateItem::getTerm)
                .toList();
    }

    // ✅ helper — add a term properly through the join entity
    public void addTerm(TcLibrary term) {
        TcTemplateItem item = new TcTemplateItem(this, term, this.orgId);
        templateItems.add(item);
    }

    // ✅ helper — remove a term
    public void removeTerm(TcLibrary term) {
        templateItems.removeIf(item -> item.getTerm().equals(term));
    }

    @Override
    public String toString() {
        return "TcTemplate{" +
                "templateId=" + templateId +
                ", orgId=" + orgId +
                ", templateName='" + templateName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
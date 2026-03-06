package com.example.demo.customer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "terms_group")
public class TermsGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", length = 255, nullable = false)
    private String groupName;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    @JsonIgnore
    private TermsTemplate termsTemplate;

    @OneToMany(mappedBy = "termsGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TermsDetail> termsDetails = new ArrayList<>();

    public TermsGroup() {}

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public TermsTemplate getTermsTemplate() { return termsTemplate; }
    public void setTermsTemplate(TermsTemplate termsTemplate) { this.termsTemplate = termsTemplate; }

    public List<TermsDetail> getTermsDetails() { return termsDetails; }
    public void setTermsDetails(List<TermsDetail> termsDetails) { this.termsDetails = termsDetails; }
}
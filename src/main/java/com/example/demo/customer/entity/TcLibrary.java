package com.example.demo.customer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tc_library")
public class TcLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long termId;

    @Column(name = "term_text", columnDefinition = "TEXT", nullable = false)
    private String termText;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private TcType tcType;

    public TcLibrary() {}

    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }
    public String getTermText() { return termText; }
    public void setTermText(String termText) { this.termText = termText; }
    public TcType getTcType() { return tcType; }
    public void setTcType(TcType tcType) { this.tcType = tcType; }
}
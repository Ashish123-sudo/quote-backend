package com.example.demo.customer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tc_type")
public class TcType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Long typeId;

    @Column(name = "type_name", length = 100, nullable = false)
    private String typeName;

    public TcType() {}

    public Long getTypeId() { return typeId; }
    public void setTypeId(Long typeId) { this.typeId = typeId; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
}
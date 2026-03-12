package com.example.demo.customer.repository;

import com.example.demo.customer.entity.TcType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TcTypeRepository extends JpaRepository<TcType, Long> {
    Optional<TcType> findByTypeName(String typeName);
}
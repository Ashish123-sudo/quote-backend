package com.example.demo.customer.repository;

import com.example.demo.customer.entity.TcLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TcLibraryRepository extends JpaRepository<TcLibrary, Long> {
    List<TcLibrary> findByTcType_TypeId(Long typeId);
}
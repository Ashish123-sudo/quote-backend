package com.example.demo.customer.repository;

import com.example.demo.customer.entity.TermsDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermsDetailRepository extends JpaRepository<TermsDetail, Long> {
    List<TermsDetail> findByTermsGroup_GroupIdOrderBySortOrderAsc(Long groupId);
}
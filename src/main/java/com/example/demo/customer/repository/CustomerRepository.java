package com.example.demo.customer.repository;

import com.example.demo.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByName(String name);
    List<Customer> findByCity(String city);
    List<Customer> findByCountry(String country);
    List<Customer> findByNameContainingIgnoreCase(String name);
}
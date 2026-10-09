package com.mabilo.withdrawal_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mabilo.withdrawal_system.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
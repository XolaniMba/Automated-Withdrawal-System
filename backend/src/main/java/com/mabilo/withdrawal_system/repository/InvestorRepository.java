package com.mabilo.withdrawal_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mabilo.withdrawal_system.model.Investor;

public interface InvestorRepository extends JpaRepository<Investor, Long> {
    
}


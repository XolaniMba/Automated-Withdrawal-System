package com.mabilo.withdrawal_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mabilo.withdrawal_system.model.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

}
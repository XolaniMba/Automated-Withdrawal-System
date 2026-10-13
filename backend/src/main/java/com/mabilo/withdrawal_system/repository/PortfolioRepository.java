package com.mabilo.withdrawal_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mabilo.withdrawal_system.model.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Optional<Portfolio> findByInvestorId(Long investorId);
}
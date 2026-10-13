package com.mabilo.withdrawal_system.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mabilo.withdrawal_system.model.Investor;
import com.mabilo.withdrawal_system.model.Portfolio;
import com.mabilo.withdrawal_system.model.Product;
import com.mabilo.withdrawal_system.repository.InvestorRepository;
import com.mabilo.withdrawal_system.repository.PortfolioRepository;
import com.mabilo.withdrawal_system.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final InvestorRepository investorRepository;
    private final PortfolioRepository portfolioRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {

        // Investor 1: over 65, can make retirement withdrawals
        Investor thabo = investorRepository.save(new Investor(null, "Thabo Mokoena", 70));
        Portfolio thaboPortfolio = portfolioRepository.save(
                new Portfolio(null, thabo, new BigDecimal("500000.00"), null));
        productRepository.save(new Product(
                null, "Retirement Annuity", "RETIREMENT", new BigDecimal("300000.00"), thaboPortfolio));
        productRepository.save(new Product(
                null, "Unit Trust", "GENERAL", new BigDecimal("200000.00"), thaboPortfolio));

        // Investor 2: under 65, retirement withdrawals should be blocked
        Investor naledi = investorRepository.save(new Investor(null, "Naledi Dlamini", 40));
        Portfolio nalediPortfolio = portfolioRepository.save(
                new Portfolio(null, naledi, new BigDecimal("120000.00"), null));
        productRepository.save(new Product(
                null, "Tax-Free Savings", "GENERAL", new BigDecimal("120000.00"), nalediPortfolio));
    }

}
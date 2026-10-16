package com.mabilo.withdrawal_system.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mabilo.withdrawal_system.dto.PortfolioDTO;
import com.mabilo.withdrawal_system.dto.ProductDTO;
import com.mabilo.withdrawal_system.exception.ResourceNotFoundException;
import com.mabilo.withdrawal_system.model.Portfolio;
import com.mabilo.withdrawal_system.repository.PortfolioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true)
    public PortfolioDTO getPortfolioByInvestorId(Long investorId) {

        Portfolio portfolio = portfolioRepository.findByInvestorId(investorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No portfolio found for investor " + investorId));

        List<ProductDTO> products = portfolio.getProducts().stream()
                .map(p -> new ProductDTO(p.getId(), p.getName(), p.getType(), p.getBalance()))
                .toList();

        return new PortfolioDTO(
                portfolio.getInvestor().getName(),
                portfolio.getInvestor().getAge(),
                portfolio.getBalance(),
                products);
    }

}
package com.mabilo.withdrawal_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mabilo.withdrawal_system.dto.PortfolioDTO;
import com.mabilo.withdrawal_system.service.PortfolioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/investors")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/{id}/portfolio")
    public PortfolioDTO getPortfolio(@PathVariable Long id) {
        return portfolioService.getPortfolioByInvestorId(id);
    }

}
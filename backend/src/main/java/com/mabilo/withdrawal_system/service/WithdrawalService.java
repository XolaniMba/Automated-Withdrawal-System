package com.mabilo.withdrawal_system.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import com.mabilo.withdrawal_system.model.WithdrawalNotice;

import com.mabilo.withdrawal_system.dto.WithdrawalRequestDTO;
import com.mabilo.withdrawal_system.dto.WithdrawalResponseDTO;
import com.mabilo.withdrawal_system.exception.ResourceNotFoundException;
import com.mabilo.withdrawal_system.model.Investor;
import com.mabilo.withdrawal_system.model.Portfolio;
import com.mabilo.withdrawal_system.repository.InvestorRepository;
import com.mabilo.withdrawal_system.repository.PortfolioRepository;
import com.mabilo.withdrawal_system.repository.WithdrawalNoticeRepository;
import com.mabilo.withdrawal_system.exception.BusinessRuleException;
import java.math.BigDecimal;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final InvestorRepository investorRepository;
    private final PortfolioRepository portfolioRepository;
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;

    public WithdrawalResponseDTO createWithdrawal(WithdrawalRequestDTO request) {

        Investor investor = investorRepository.findById(request.getInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Investor not found with id " + request.getInvestorId()));

        Portfolio portfolio = portfolioRepository.findByInvestorId(investor.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No portfolio found for investor " + investor.getId()));

        if ("RETIREMENT".equals(request.getType()) && investor.getAge() <= 65) {
             throw new BusinessRuleException(
            "Retirement withdrawals are only allowed for investors over 65");
            }
        if (request.getAmount().compareTo(portfolio.getBalance()) > 0) {
            throw new BusinessRuleException("Withdrawal amount cannot exceed your balance");
        }
       BigDecimal maxAllowed = portfolio.getBalance().multiply(new BigDecimal("0.90"));

        if (request.getAmount().compareTo(maxAllowed) > 0) {
             throw new BusinessRuleException(
            "Withdrawal amount cannot exceed 90% of your balance");
}
        WithdrawalNotice notice = new WithdrawalNotice();
        notice.setInvestor(investor);
        notice.setAmount(request.getAmount());
        notice.setType(request.getType());
        notice.setStatus("PENDING");
        notice.setDateRequested(LocalDateTime.now());

        WithdrawalNotice saved = withdrawalNoticeRepository.save(notice);

        return new WithdrawalResponseDTO(
                saved.getId(),
                saved.getStatus(),
                saved.getType(),
                saved.getAmount(),
                saved.getDateRequested());
    }

}
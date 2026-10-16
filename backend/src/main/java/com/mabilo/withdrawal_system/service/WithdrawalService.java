package com.mabilo.withdrawal_system.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mabilo.withdrawal_system.dto.WithdrawalRequestDTO;
import com.mabilo.withdrawal_system.dto.WithdrawalResponseDTO;
import com.mabilo.withdrawal_system.exception.BusinessRuleException;
import com.mabilo.withdrawal_system.exception.ResourceNotFoundException;
import com.mabilo.withdrawal_system.model.Investor;
import com.mabilo.withdrawal_system.model.Portfolio;
import com.mabilo.withdrawal_system.model.WithdrawalNotice;
import com.mabilo.withdrawal_system.repository.InvestorRepository;
import com.mabilo.withdrawal_system.repository.PortfolioRepository;
import com.mabilo.withdrawal_system.repository.WithdrawalNoticeRepository;
import java.time.LocalDate;

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
            throw new BusinessRuleException(
                    "Withdrawal amount cannot exceed your balance");
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

    public List<WithdrawalResponseDTO> getHistory(Long investorId) {

        if (!investorRepository.existsById(investorId)) {
            throw new ResourceNotFoundException("Investor not found with id " + investorId);
        }

        return withdrawalNoticeRepository
                .findByInvestorIdOrderByDateRequestedDesc(investorId).stream()
                .map(w -> new WithdrawalResponseDTO(
                        w.getId(), w.getStatus(), w.getType(), w.getAmount(), w.getDateRequested()))
                .toList();
    }
public String exportCsv(Long investorId, String type, LocalDate from, LocalDate to) {

    if (!investorRepository.existsById(investorId)) {
        throw new ResourceNotFoundException("Investor not found with id " + investorId);
    }

    List<WithdrawalNotice> notices =
            withdrawalNoticeRepository.findByInvestorIdOrderByDateRequestedDesc(investorId);

    StringBuilder csv = new StringBuilder("Id,Type,Amount,Status,Date Requested\n");

    for (WithdrawalNotice w : notices) {

// Filter by withdrawal type
    if (type != null && !type.equalsIgnoreCase(w.getType())) {
        continue;
    }

    // Filter by start date
    if (from != null && w.getDateRequested().toLocalDate().isBefore(from)) {
        continue;
    }

    // Filter by end date
    if (to != null && w.getDateRequested().toLocalDate().isAfter(to)) {
        continue;
    }
    
        csv.append(w.getId()).append(',')
                .append(w.getType()).append(',')
                .append(w.getAmount()).append(',')
                .append(w.getStatus()).append(',')
                .append(w.getDateRequested()).append('\n');
    }

    return csv.toString();
}
}

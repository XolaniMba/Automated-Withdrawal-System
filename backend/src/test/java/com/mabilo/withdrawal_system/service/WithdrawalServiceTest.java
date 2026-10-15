package com.mabilo.withdrawal_system.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mabilo.withdrawal_system.exception.BusinessRuleException;
import com.mabilo.withdrawal_system.exception.ResourceNotFoundException;

import com.mabilo.withdrawal_system.dto.WithdrawalRequestDTO;
import com.mabilo.withdrawal_system.dto.WithdrawalResponseDTO;
import com.mabilo.withdrawal_system.model.Investor;
import com.mabilo.withdrawal_system.model.Portfolio;
import com.mabilo.withdrawal_system.model.WithdrawalNotice;
import com.mabilo.withdrawal_system.repository.InvestorRepository;
import com.mabilo.withdrawal_system.repository.PortfolioRepository;
import com.mabilo.withdrawal_system.repository.WithdrawalNoticeRepository;

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @Mock
    private InvestorRepository investorRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    @InjectMocks
    private WithdrawalService withdrawalService;

    @Test
    void createWithdrawal_validRequest_savesPendingNotice() {
        // Arrange
        Investor investor = new Investor(1L, "Thabo Mokoena", 70);
        Portfolio portfolio = new Portfolio(1L, investor, new BigDecimal("500000.00"), null);
        WithdrawalRequestDTO request =
                new WithdrawalRequestDTO(1L, new BigDecimal("100000"), "RETIREMENT");

        when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));
        when(portfolioRepository.findByInvestorId(1L)).thenReturn(Optional.of(portfolio));
        when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                .thenAnswer(invocation -> {
                    WithdrawalNotice notice = invocation.getArgument(0);
                    notice.setId(10L);
                    return notice;
                });

        // Act
        WithdrawalResponseDTO response = withdrawalService.createWithdrawal(request);

        // Assert
        assertEquals("PENDING", response.getStatus());
        assertEquals("RETIREMENT", response.getType());
        assertEquals(new BigDecimal("100000"), response.getAmount());
        assertEquals(10L, response.getId());
    }
    @Test
void retirementWithdrawal_age65_isRejected() {
    // Arrange: exactly 65 is NOT "over 65"
    Investor investor = new Investor(1L, "Test Investor", 65);
    Portfolio portfolio = new Portfolio(1L, investor, new BigDecimal("500000.00"), null);
    WithdrawalRequestDTO request =
            new WithdrawalRequestDTO(1L, new BigDecimal("10000"), "RETIREMENT");

    when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));
    when(portfolioRepository.findByInvestorId(1L)).thenReturn(Optional.of(portfolio));

    // Act + Assert
    BusinessRuleException ex = assertThrows(BusinessRuleException.class,
            () -> withdrawalService.createWithdrawal(request));

    assertEquals("Retirement withdrawals are only allowed for investors over 65", ex.getMessage());
}

@Test
void generalWithdrawal_exceedsBalance_isRejected() {
    // Arrange: exactly 40 and can not withdraw more than 90% balance
    Investor investor = new Investor(1L, "Test Investor", 40);
    Portfolio portfolio = new Portfolio(1L, investor, new BigDecimal("100000.00"), null);
    WithdrawalRequestDTO request =
            new WithdrawalRequestDTO(1L, new BigDecimal("150000"), "GENERAL");

    when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));
    when(portfolioRepository.findByInvestorId(1L)).thenReturn(Optional.of(portfolio));

    // Act + Assert
    BusinessRuleException ex = assertThrows(BusinessRuleException.class,
            () -> withdrawalService.createWithdrawal(request));

    assertEquals("Withdrawal amount cannot exceed your balance", ex.getMessage());}

}
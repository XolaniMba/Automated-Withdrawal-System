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

}
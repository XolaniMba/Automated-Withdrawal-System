package com.mabilo.withdrawal_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mabilo.withdrawal_system.model.WithdrawalNotice;
import java.util.List;

public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {
    List<WithdrawalNotice> findByInvestorIdOrderByDateRequestedDesc(Long investorId);
    
}

package com.mabilo.withdrawal_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mabilo.withdrawal_system.model.WithdrawalNotice;

public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {
    
}

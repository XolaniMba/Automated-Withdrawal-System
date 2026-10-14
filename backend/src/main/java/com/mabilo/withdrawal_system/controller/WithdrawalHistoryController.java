package com.mabilo.withdrawal_system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mabilo.withdrawal_system.dto.WithdrawalResponseDTO;
import com.mabilo.withdrawal_system.service.WithdrawalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/investors")
@RequiredArgsConstructor
public class WithdrawalHistoryController {

    private final WithdrawalService withdrawalService;

    @GetMapping("/{id}/withdrawals")
    public List<WithdrawalResponseDTO> getHistory(@PathVariable Long id) {
        return withdrawalService.getHistory(id);
    }

}
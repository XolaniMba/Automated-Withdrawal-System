package com.mabilo.withdrawal_system.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mabilo.withdrawal_system.dto.WithdrawalRequestDTO;
import com.mabilo.withdrawal_system.dto.WithdrawalResponseDTO;
import com.mabilo.withdrawal_system.service.WithdrawalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping
    public ResponseEntity<WithdrawalResponseDTO> createWithdrawal(
            @Valid @RequestBody WithdrawalRequestDTO request) {

        WithdrawalResponseDTO response = withdrawalService.createWithdrawal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
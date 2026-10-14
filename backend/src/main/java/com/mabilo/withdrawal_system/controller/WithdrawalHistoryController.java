package com.mabilo.withdrawal_system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mabilo.withdrawal_system.dto.WithdrawalResponseDTO;
import com.mabilo.withdrawal_system.service.WithdrawalService;

import lombok.RequiredArgsConstructor;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/investors")
@RequiredArgsConstructor
public class WithdrawalHistoryController {

    private final WithdrawalService withdrawalService;

    @GetMapping("/{id}/withdrawals")
    public List<WithdrawalResponseDTO> getHistory(@PathVariable Long id) {
        return withdrawalService.getHistory(id);
    }
    @GetMapping("/{id}/withdrawals/export")
public ResponseEntity<String> exportCsv(
        @PathVariable Long id,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    String csv = withdrawalService.exportCsv(id, type, from, to);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=withdrawals.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv);
}

}
package com.mabilo.withdrawal_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalResponseDTO {

    private Long id;

    private String status;

    private String type;

    private BigDecimal amount;
    
    private LocalDateTime dateRequested;

}
package com.mabilo.withdrawal_system.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDTO {

    private String investorName;

    private int investorAge;

    private BigDecimal balance;

    private List<ProductDTO> products;

}
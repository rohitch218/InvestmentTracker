package com.investtracker.portfolio.dto;

import com.investtracker.portfolio.entity.Investment.InvestmentType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class InvestmentResponse {
    private Long id;
    private String tenantId;
    private Long userId;
    private String name;
    private InvestmentType type;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private BigDecimal currentPrice;
    private BigDecimal investedAmount;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private BigDecimal profitLossPct;
    private LocalDate purchaseDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

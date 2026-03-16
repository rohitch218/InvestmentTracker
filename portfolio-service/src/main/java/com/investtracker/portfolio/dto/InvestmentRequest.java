package com.investtracker.portfolio.dto;

import com.investtracker.portfolio.entity.Investment.InvestmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvestmentRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Investment type is required")
    private InvestmentType type;

    private String symbol;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantity;

    @NotNull(message = "Purchase price is required")
    @Positive(message = "Purchase price must be positive")
    private BigDecimal purchasePrice;

    @NotNull(message = "Current price is required")
    @Positive(message = "Current price must be positive")
    private BigDecimal currentPrice;

    @NotNull(message = "Purchase date is required")
    private LocalDate purchaseDate;

    private String notes;
}

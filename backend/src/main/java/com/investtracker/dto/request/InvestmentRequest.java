package com.investtracker.dto.request;

import com.investtracker.entity.InvestmentType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * InvestmentRequest — Shared request DTO for both Create and Update.
 */
@Data
public class InvestmentRequest {

    @NotBlank(message = "Investment name is required")
    @Size(max = 150)
    private String name;

    @NotNull(message = "Investment type is required")
    private InvestmentType type;

    @Size(max = 20)
    private String symbol;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00000001", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.0001", message = "Purchase price must be greater than 0")
    private BigDecimal purchasePrice;

    @NotNull(message = "Current price is required")
    @DecimalMin(value = "0.0001", message = "Current price must be greater than 0")
    private BigDecimal currentPrice;

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDate purchaseDate;

    @Size(max = 1000)
    private String notes;
}

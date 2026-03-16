package com.investtracker.transaction.dto;

import com.investtracker.transaction.entity.Transaction.TransactionType;
import com.investtracker.transaction.entity.Transaction.TransactionSource;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private String tenantId;
    private Long userId;
    private Long investmentId;
    private TransactionType transactionType;
    private BigDecimal quantity;
    private BigDecimal price;
    private LocalDate transactionDate;
    private TransactionSource source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

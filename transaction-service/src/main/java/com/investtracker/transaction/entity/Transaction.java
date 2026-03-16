package com.investtracker.transaction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Transaction — Core business entity in the Transaction Service.
 * 
 * In a decoupled microservices architecture:
 * - Direct @ManyToOne relationships to Investment or User are replaced 
 *   by ID propagation (investmentId, userId, tenantId).
 */
@Entity
@Table(
    name = "transactions",
    indexes = {
        @Index(name = "idx_txn_tenant",     columnList = "tenant_id"),
        @Index(name = "idx_txn_investment", columnList = "investment_id, tenant_id"),
        @Index(name = "idx_txn_date",       columnList = "transaction_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", length = 36, nullable = false, updatable = false)
    private String tenantId;

    @Column(name = "investment_id", nullable = false)
    private Long investmentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(name = "quantity", precision = 18, scale = 8, nullable = false)
    private BigDecimal quantity;

    @Column(name = "price", precision = 18, scale = 4, nullable = false)
    private BigDecimal price;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 20, nullable = false)
    @Builder.Default
    private TransactionSource source = TransactionSource.MANUAL;

    public enum TransactionType {
        BUY, SELL, DIVIDEND, SPLIT
    }

    public enum TransactionSource {
        MANUAL, CSV_UPLOAD
    }
}

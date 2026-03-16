package com.investtracker.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Investment — Core business entity in the Portfolio Service.
 * 
 * In a microservices architecture:
 * - Direct @ManyToOne relationships to User or Tenant entities from other 
 *   services are replaced by ID propagation (userId, tenantId).
 * - This provides decoupling and independent data scaling.
 */
@Entity
@Table(
    name = "investments",
    indexes = {
        @Index(name = "idx_inv_tenant",    columnList = "tenant_id"),
        @Index(name = "idx_inv_user",      columnList = "user_id, tenant_id"),
        @Index(name = "idx_inv_type",      columnList = "type, tenant_id"),
        @Index(name = "idx_inv_deleted",   columnList = "deleted_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Investment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", length = 36, nullable = false, updatable = false)
    private String tenantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private InvestmentType type;

    @Column(name = "symbol", length = 20)
    private String symbol;

    @Column(name = "quantity", precision = 18, scale = 8, nullable = false)
    private BigDecimal quantity;

    @Column(name = "purchase_price", precision = 18, scale = 4, nullable = false)
    private BigDecimal purchasePrice;

    @Column(name = "current_price", precision = 18, scale = 4, nullable = false)
    private BigDecimal currentPrice;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ── Business Logic ──────────────────────────────────────────────────

    public BigDecimal getInvestedAmount() {
        return purchasePrice.multiply(quantity);
    }

    public BigDecimal getCurrentValue() {
        return currentPrice.multiply(quantity);
    }

    public BigDecimal getProfitLoss() {
        return getCurrentValue().subtract(getInvestedAmount());
    }

    public BigDecimal getProfitLossPct() {
        if (getInvestedAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getProfitLoss()
            .divide(getInvestedAmount(), 4, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public enum InvestmentType {
        STOCK, MUTUAL_FUND, CRYPTO, FIXED_DEPOSIT
    }
}

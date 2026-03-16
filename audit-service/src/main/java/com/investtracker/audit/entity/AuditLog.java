package com.investtracker.audit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * AuditLog — Immutable event record for every system mutation.
 */
@Entity
@Table(
    name = "audit_logs",
    indexes = {
        @Index(name = "idx_audit_tenant",   columnList = "tenant_id"),
        @Index(name = "idx_audit_user",     columnList = "user_id, tenant_id"),
        @Index(name = "idx_audit_entity",   columnList = "entity_type, entity_id, tenant_id"),
        @Index(name = "idx_audit_occurred", columnList = "occurred_at")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", length = 36, nullable = false, updatable = false)
    private String tenantId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "entity_type", length = 50, nullable = false, updatable = false)
    private String entityType;

    @Column(name = "entity_id", updatable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", length = 20, nullable = false, updatable = false)
    private AuditAction action;

    @Column(name = "old_value", columnDefinition = "TEXT") // Simplified for compatibility
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT") // Simplified for compatibility
    private String newValue;

    @Column(name = "ip_address", length = 45, updatable = false)
    private String ipAddress;

    @Column(name = "user_agent", length = 255, updatable = false)
    private String userAgent;

    @CreatedDate
    @Column(name = "occurred_at", nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    public enum AuditAction {
        CREATE, UPDATE, DELETE, LOGIN, LOGOUT, CSV_UPLOAD
    }
}

package com.investtracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Tenant — Represents one organisation/company using the platform.
 *
 * WHY VARCHAR(36) UUID as PK: Prevents sequential-ID enumeration attacks.
 * A malicious actor cannot guess another tenant's ID by incrementing integers.
 *
 * WHY not extend BaseEntity: Tenant manages its own timestamps explicitly
 * because it uses a UUID PK (not a @GeneratedValue BIGINT), so we keep
 * full control.
 */
@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;   // UUID assigned in service layer before persist

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "domain", length = 100, unique = true)
    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 20)
    @Builder.Default
    private TenantPlan plan = TenantPlan.FREE;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships (not fetched eagerly — lazy by default for collections)
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum TenantPlan {
        FREE, PRO, ENTERPRISE
    }
}

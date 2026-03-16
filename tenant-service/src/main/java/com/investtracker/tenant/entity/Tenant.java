package com.investtracker.tenant.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant extends BaseEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id; // UUID string

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", length = 20, nullable = false)
    @Builder.Default
    private TenantPlan plan = TenantPlan.FREE;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // Subscription tracking fields
    @Column(name = "subscription_id", length = 50)
    private String subscriptionId;

    public enum TenantPlan {
        FREE, PREMIUM, ENTERPRISE
    }
}

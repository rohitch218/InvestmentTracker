package com.investtracker.portfolio.tenant;

import lombok.extern.slf4j.Slf4j;

/**
 * TenantContext — Stores the current request's tenantId in a ThreadLocal variable.
 * 
 * Crucial for data isolation in multi-tenant microservices.
 */
@Slf4j
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        log.trace("Setting tenantId to: {}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}

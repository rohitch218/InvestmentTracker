package com.investtracker.tenant;

/**
 * TenantContext — Thread-local store for the current request's tenant ID.
 *
 * WHY ThreadLocal: In a servlet container, each HTTP request runs on its own
 * thread. ThreadLocal gives us a per-thread variable — perfect for storing
 * the tenant ID extracted from the JWT so it's available anywhere downstream
 * (services, repositories) without passing it as a method parameter.
 *
 * WHY critical cleanup: Thread pools REUSE threads. If we don't call clear()
 * after the request, the next request on that thread will inherit the previous
 * request's tenant ID → CATASTROPHIC cross-tenant data leak.
 *
 * The cleanup is guaranteed by TenantFilter's finally block.
 */
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
        // Utility class — no instantiation
    }

    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();   // remove() is safer than set(null) — it truly frees the value
    }
}

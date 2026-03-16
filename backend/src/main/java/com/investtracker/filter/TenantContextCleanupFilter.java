package com.investtracker.filter;

import com.investtracker.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * TenantContextCleanupFilter — Runs AFTER JwtAuthFilter to guarantee
 * that ThreadLocal TenantContext is always cleared regardless of errors.
 *
 * WHY a separate filter instead of cleanup in JwtAuthFilter?
 * If a controller or service throws an uncaught exception, the JwtAuthFilter's
 * chain call might propagate the exception before reaching the finally block.
 * A dedicated teardown filter with @Order guarantees cleanup happens last.
 *
 * @Order(Integer.MAX_VALUE) ensures this runs last in the filter chain.
 */
@Component
@Order(Integer.MAX_VALUE)
public class TenantContextCleanupFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            // This ALWAYS runs — even on exception or redirect
            TenantContext.clear();
        }
    }
}

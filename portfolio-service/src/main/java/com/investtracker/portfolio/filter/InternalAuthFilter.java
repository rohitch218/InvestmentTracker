package com.investtracker.portfolio.filter;

import com.investtracker.portfolio.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * InternalAuthFilter — For Domain Services (e.g. Portfolio).
 * 
 * Instead of calling Auth DB, this microservice filter "trusts" identity 
 * headers propagated from the API Gateway.
 */
@Slf4j
@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String email = request.getHeader("X-User-Email");
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");
        String tenantId = request.getHeader("X-Tenant-Id");

        if (email != null && tenantId != null && userId != null) {
            
            // Re-establish contexts in domain service without database call
            TenantContext.setTenantId(tenantId);
            
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Construct Authentication from headers
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email, // principal (email used as name)
                    userId, // credentials (ID stored here)
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // CRITICAL: Clear for next request (ThreadLocal)
            TenantContext.clear();
        }
    }
}

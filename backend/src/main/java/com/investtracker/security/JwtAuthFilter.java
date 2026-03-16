package com.investtracker.security;

import com.investtracker.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter — Intercepts every HTTP request to validate the JWT and
 * establish the Spring Security authentication context + tenant context.
 *
 * Extends OncePerRequestFilter to guarantee exactly one execution per request
 * (Spring may dispatch internally multiple times for error handling etc.)
 *
 * Flow:
 *  1. Extract "Bearer <token>" from Authorization header
 *  2. Parse and validate the JWT
 *  3. Load UserDetails from DB (to ensure user still exists/is active)
 *  4. Set Authentication in SecurityContext → Spring Security treats user as logged in
 *  5. Set tenantId in TenantContext → available to all downstream services
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // No auth header or not a Bearer token → skip (public endpoint or invalid)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);    // "Bearer " is 7 chars

        try {
            final String email    = jwtUtil.extractEmail(jwt);
            final String tenantId = jwtUtil.extractTenantId(jwt);
            final String tokenType = jwtUtil.extractTokenType(jwt);

            // Refuse refresh tokens used as access tokens
            if ("REFRESH".equals(tokenType)) {
                log.warn("Refresh token used as access token for email: {}", email);
                filterChain.doFilter(request, response);
                return;
            }

            // Only process if not already authenticated in this request
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtUtil.isTokenValid(jwt, userDetails)) {

                    // Set Spring Security authentication
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Set tenant context for this thread
                    TenantContext.setTenantId(tenantId);
                }
            }

        } catch (Exception e) {
            // Log but don't throw — let Spring Security handle the 401 response
            log.warn("JWT processing error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
        // NOTE: TenantContext.clear() is NOT called here.
        // It is called in TenantContextCleanupFilter which runs after this filter
        // to ensure it cleans up even if exceptions occur later in the chain.
    }
}

package com.investtracker.security;

import com.investtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CustomUserDetailsService — Loads user by email for Spring Security.
 *
 * WHY look up by email only (not email+tenantId)?
 * Because the JWT already contains both email and tenantId.
 * When the JwtAuthFilter calls loadUserByUsername(email), the tenant context
 * has already been set from the token. The email in the DB is globally
 * unique enough to identify the user — two same-email users in different
 * tenants would require disambiguation, but our JWT contains the userId
 * claim which is globally unique (BIGINT PK).
 *
 * In a stricter implementation you could look up by (email, tenantId)
 * using TenantContext.getTenantId() here.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found with email: " + email
            ));
    }
}

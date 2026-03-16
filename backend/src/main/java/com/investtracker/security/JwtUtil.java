package com.investtracker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtUtil — Centralized JWT creation and validation.
 *
 * WHY HMAC-SHA256 (HS256): For a monolith/single-service system, a shared
 * symmetric secret is simpler and sufficient. If we later split into
 * microservices that need to verify tokens independently, we would upgrade
 * to RS256 (asymmetric) so only the auth service holds the private key.
 *
 * WHY we embed tenantId + role in the token: Eliminates a DB round-trip on
 * every request. The filter can resolve tenant isolation from the token alone.
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiry-ms}")
    private long accessTokenExpiryMs;

    @Value("${app.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    // ── Token generation ─────────────────────────────────────────────────

    /**
     * Generates a signed access token containing user info and tenant ID.
     *
     * @param userId   DB primary key of the user
     * @param email    User's email (used as JWT subject/principal)
     * @param role     ADMIN or USER
     * @param tenantId UUID of the tenant the user belongs to
     */
    public String generateAccessToken(Long userId, String email, String role, String tenantId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",   userId);
        claims.put("role",     role);
        claims.put("tenantId", tenantId);
        claims.put("type",     "ACCESS");

        return buildToken(claims, email, accessTokenExpiryMs);
    }

    public String generateRefreshToken(Long userId, String email, String tenantId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",   userId);
        claims.put("tenantId", tenantId);
        claims.put("type",     "REFRESH");

        return buildToken(claims, email, refreshTokenExpiryMs);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiryMs) {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(subject)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiryMs))
            .signWith(getSigningKey())
            .compact();
    }

    // ── Token extraction ─────────────────────────────────────────────────

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTenantId(String token) {
        return extractClaim(token, claims -> claims.get("tenantId", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    // ── Token validation ─────────────────────────────────────────────────

    /**
     * Validates the token against the UserDetails loaded from DB.
     * Checks: signature, expiry, and email match.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // ── Internal ─────────────────────────────────────────────────────────

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Derives a SecretKey from the configured secret string.
     * The string must be at least 32 characters for HS256.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

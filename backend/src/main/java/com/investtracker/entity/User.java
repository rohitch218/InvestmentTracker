package com.investtracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * User — Platform user belonging to a specific Tenant.
 *
 * WHY implements UserDetails: Spring Security requires a UserDetails
 * object for authentication. By implementing it directly on the entity,
 * we avoid an extra wrapper class and keep the code lean.
 *
 * WHY (email, tenant_id) unique: The same person might sign up for
 * two different company accounts with the same email — both are valid.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_email_tenant",
        columnNames = {"email", "tenant_id"}
    ),
    indexes = @Index(name = "idx_users_tenant", columnList = "tenant_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", length = 36, nullable = false, updatable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", insertable = false, updatable = false)
    private Tenant tenant;

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "email", length = 150, nullable = false)
    private String email;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // ── UserDetails implementation ────────────────────────────────────────

    /**
     * Spring Security uses this for authentication.
     * We return passwordHash (BCrypt digest) — Spring compares it
     * with the raw password using BCryptPasswordEncoder.
     */
    @Override
    public String getPassword() {
        return passwordHash;
    }

    /**
     * Spring Security uses this as the principal identifier.
     * We use email (globally unique within tenant).
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Prefix "ROLE_" is required by Spring Security's hasRole() checks
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired()    { return true; }

    @Override
    public boolean isAccountNonLocked()     { return isActive; }

    @Override
    public boolean isCredentialsNonExpired(){ return true; }

    @Override
    public boolean isEnabled()              { return isActive; }

    // ── Helper ────────────────────────────────────────────────────────────

    /** Convenience accessor for display/logging (avoids confusion with getUsername) */
    public String getDisplayName() {
        return username;
    }

    public enum Role {
        ADMIN, USER
    }
}

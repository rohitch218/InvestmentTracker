package com.investtracker.repository;

import com.investtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository — Spring Data JPA repository for User entity.
 *
 * WHY no explicit @Query: Spring Data derives the SQL from method names.
 * "findByEmailAndTenantId" → WHERE email = ? AND tenant_id = ?
 * This is concise, type-safe, and automatically parameterized (no SQL injection).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndTenantId(String email, String tenantId);

    Optional<User> findByEmail(String email); // for JWT lookup (email is in token)

    List<User> findAllByTenantId(String tenantId);

    boolean existsByEmailAndTenantId(String email, String tenantId);
}

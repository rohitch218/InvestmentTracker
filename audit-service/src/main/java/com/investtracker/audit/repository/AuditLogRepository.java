package com.investtracker.audit.repository;

import com.investtracker.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAllByTenantId(String tenantId, Pageable pageable);

    Page<AuditLog> findAllByTenantIdAndUserId(String tenantId, Long userId, Pageable pageable);

    Page<AuditLog> findAllByTenantIdAndEntityTypeAndEntityId(
        String tenantId, String entityType, Long entityId, Pageable pageable
    );
}

package com.investtracker.audit.service;

import com.investtracker.audit.dto.AuditRequest;
import com.investtracker.audit.entity.AuditLog;
import com.investtracker.audit.repository.AuditLogRepository;
import com.investtracker.audit.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_AUDIT)
    @Transactional
    public void consumeAuditLog(AuditRequest request, @Header("X-Tenant-Id") String tenantId) {
        log.info("Consumed audit message via RabbitMQ: {} on {} for tenant: {}", 
            request.getAction(), request.getEntityType(), tenantId);
        processLog(request, tenantId);
    }

    @Async
    @Transactional
    public void processLog(AuditRequest request, String tenantId) {
        try {
            AuditLog entry = AuditLog.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .action(request.getAction())
                .oldValue(request.getOldValue())
                .newValue(request.getNewValue())
                .ipAddress(request.getIpAddress())
                .userAgent(request.getUserAgent())
                .build();

            auditLogRepository.save(entry);
            log.debug("Processed audit log: {} on {} {} for tenant: {}", 
                entry.getAction(), entry.getEntityType(), entry.getEntityId(), tenantId);
        } catch (Exception e) {
            log.error("Failed to process audit log: {}", e.getMessage());
        }
    }
}

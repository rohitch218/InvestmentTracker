package com.investtracker.auth.client;

import com.investtracker.auth.dto.audit.AuditRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditClient {

    private final RabbitTemplate rabbitTemplate;

    public static final String EXCHANGE_AUDIT = "audit.exchange";
    public static final String ROUTING_KEY_AUDIT = "audit.routing.key";

    @Async
    public void log(AuditRequest request, String tenantId) {
        try {
            log.debug("Sending audit event for tenant {}: {} on {}", 
                tenantId, request.getAction(), request.getEntityType());
                
            rabbitTemplate.convertAndSend(EXCHANGE_AUDIT, ROUTING_KEY_AUDIT, request, m -> {
                m.getMessageProperties().setHeader("X-Tenant-Id", tenantId);
                return m;
            });
        } catch (Exception e) {
            log.error("Failed to enqueue audit message from Auth Service: {}", e.getMessage());
        }
    }
}

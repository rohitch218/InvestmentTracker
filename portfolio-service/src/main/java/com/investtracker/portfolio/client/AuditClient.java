package com.investtracker.portfolio.client;

import com.investtracker.portfolio.dto.audit.AuditRequest;
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
                
            // We pass the tenantId in the message header or just include it in the DTO if we update the DTO
            // For Phase 5, I'll pass it as a message post-processor to match the @Header in the listener
            rabbitTemplate.convertAndSend(EXCHANGE_AUDIT, ROUTING_KEY_AUDIT, request, m -> {
                m.getMessageProperties().setHeader("X-Tenant-Id", tenantId);
                return m;
            });
        } catch (Exception e) {
            log.error("Failed to enqueue audit message: {}", e.getMessage());
        }
    }
}

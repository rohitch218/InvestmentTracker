package com.investtracker.audit.controller;

import com.investtracker.audit.dto.AuditRequest;
import com.investtracker.audit.service.AuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @PostMapping("/log")
    public ResponseEntity<Void> logEvent(
        @Valid @RequestBody AuditRequest request,
        @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        // Enqueue asynchronously to the service
        auditService.processLog(request, tenantId);
        
        // Return 202 Accepted — audit logging should not block callers
        return ResponseEntity.accepted().build();
    }
}

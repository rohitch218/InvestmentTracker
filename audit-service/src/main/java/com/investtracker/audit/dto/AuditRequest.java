package com.investtracker.audit.dto;

import com.investtracker.audit.entity.AuditLog.AuditAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuditRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Entity type is required")
    private String entityType;

    private Long entityId;

    @NotNull(message = "Audit action is required")
    private AuditAction action;

    private String oldValue; // Serialized JSON
    private String newValue; // Serialized JSON
    private String ipAddress;
    private String userAgent;
}

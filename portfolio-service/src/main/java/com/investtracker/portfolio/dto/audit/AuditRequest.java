package com.investtracker.portfolio.dto.audit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String entityType;

    private Long entityId;

    @NotNull
    private AuditAction action;

    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String userAgent;

    public enum AuditAction {
        CREATE, UPDATE, DELETE, LOGIN, LOGOUT, CSV_UPLOAD
    }
}

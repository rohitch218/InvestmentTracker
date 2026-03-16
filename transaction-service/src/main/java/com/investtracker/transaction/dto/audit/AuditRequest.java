package com.investtracker.transaction.dto.audit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditRequest {
    private Long userId;
    private String entityType;
    private Long entityId;
    private AuditAction action;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String userAgent;

    public enum AuditAction {
        CREATE, UPDATE, DELETE, LOGIN, LOGOUT, CSV_UPLOAD
    }
}

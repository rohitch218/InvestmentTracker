package com.investtracker.tenant.controller;

import com.investtracker.tenant.entity.Tenant;
import com.investtracker.tenant.service.TenantService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@RequestBody TenantRequest request) {
        Tenant tenant = tenantService.createTenant(request.getName());
        return ResponseEntity.ok(TenantResponse.builder()
            .id(tenant.getId())
            .name(tenant.getName())
            .isActive(tenant.getIsActive())
            .plan(tenant.getPlan().name())
            .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantResponse> getTenant(@PathVariable String id) {
        Tenant tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(TenantResponse.builder()
            .id(tenant.getId())
            .name(tenant.getName())
            .isActive(tenant.getIsActive())
            .plan(tenant.getPlan().name())
            .build());
    }

    @Data
    public static class TenantRequest {
        private String name;
    }

    @Data
    @Builder
    public static class TenantResponse {
        private String id;
        private String name;
        private String plan;
        private Boolean isActive;
    }
}

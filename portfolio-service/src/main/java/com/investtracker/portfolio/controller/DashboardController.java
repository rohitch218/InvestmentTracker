package com.investtracker.portfolio.controller;

import com.investtracker.portfolio.dto.DashboardResponse;
import com.investtracker.portfolio.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard") // Using v1 for microservice consistency
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @RequestHeader("X-User-Id") Long userId
    ) {
        DashboardResponse response = dashboardService.getDashboard(tenantId, userId);
        return ResponseEntity.ok(response);
    }
}

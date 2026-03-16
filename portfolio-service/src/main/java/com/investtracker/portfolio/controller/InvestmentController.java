package com.investtracker.portfolio.controller;

import com.investtracker.portfolio.dto.InvestmentRequest;
import com.investtracker.portfolio.dto.InvestmentResponse;
import com.investtracker.portfolio.entity.Investment.InvestmentType;
import com.investtracker.portfolio.service.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/investments") // Re-adding v1 for consistency with other services
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @GetMapping
    public ResponseEntity<Page<InvestmentResponse>> getAll(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @RequestHeader("X-User-Id") Long userId,
        @RequestParam(required = false) InvestmentType type,
        @RequestParam(required = false) String search,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<InvestmentResponse> page = investmentService.getInvestments(
            tenantId, userId, type, search, pageable
        );
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentResponse> getById(
        @PathVariable Long id,
        @RequestHeader("X-Tenant-Id") String tenantId,
        @RequestHeader("X-User-Id") Long userId
    ) {
        InvestmentResponse response = investmentService.getById(id, tenantId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<InvestmentResponse> create(
        @Valid @RequestBody InvestmentRequest request,
        @RequestHeader("X-Tenant-Id") String tenantId,
        @RequestHeader("X-User-Id") Long userId
    ) {
        InvestmentResponse response = investmentService.create(request, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestmentResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody InvestmentRequest request,
        @RequestHeader("X-Tenant-Id") String tenantId,
        @RequestHeader("X-User-Id") Long userId
    ) {
        InvestmentResponse response = investmentService.update(id, request, tenantId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @RequestHeader("X-Tenant-Id") String tenantId,
        @RequestHeader("X-User-Id") Long userId
    ) {
        investmentService.delete(id, tenantId, userId);
        return ResponseEntity.noContent().build();
    }
}

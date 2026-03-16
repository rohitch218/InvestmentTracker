package com.investtracker.transaction.controller;

import com.investtracker.transaction.dto.TransactionRequest;
import com.investtracker.transaction.dto.TransactionResponse;
import com.investtracker.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getHistory(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @RequestHeader("X-User-Id") Long userId,
        @PageableDefault(size = 20, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TransactionResponse> page = transactionService.getHistory(tenantId, userId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/investment/{investmentId}")
    public ResponseEntity<List<TransactionResponse>> getByInvestment(
        @RequestHeader("X-Tenant-Id") String tenantId,
        @PathVariable Long investmentId
    ) {
        List<TransactionResponse> list = transactionService.getByInvestment(investmentId, tenantId);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> logTransaction(
        @Valid @RequestBody TransactionRequest request,
        @RequestHeader("X-Tenant-Id") String tenantId,
        @RequestHeader("X-User-Id") Long userId
    ) {
        TransactionResponse response = transactionService.logTransaction(request, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

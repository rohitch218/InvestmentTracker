package com.investtracker.transaction.service;

import com.investtracker.transaction.client.AuditClient;
import com.investtracker.transaction.dto.TransactionRequest;
import com.investtracker.transaction.dto.TransactionResponse;
import com.investtracker.transaction.dto.audit.AuditRequest;
import com.investtracker.transaction.entity.Transaction;
import com.investtracker.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TransactionService — Refactored for Phase 5 (Event-Driven Auditing).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AuditClient        auditClient;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getHistory(String tenantId, Long userId, Pageable pageable) {
        return transactionRepository
            .findAllByTenantIdAndUserId(tenantId, userId, pageable)
            .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getByInvestment(Long investmentId, String tenantId) {
        return transactionRepository
            .findAllByInvestmentIdAndTenantId(investmentId, tenantId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponse logTransaction(TransactionRequest request, String tenantId, Long userId) {
        Transaction txn = Transaction.builder()
            .tenantId(tenantId)
            .userId(userId)
            .investmentId(request.getInvestmentId())
            .transactionType(request.getTransactionType())
            .quantity(request.getQuantity())
            .price(request.getPrice())
            .transactionDate(request.getTransactionDate())
            .source(Transaction.TransactionSource.MANUAL)
            .build();

        Transaction saved = transactionRepository.save(txn);
        
        // Asynchronous Audit logging via RabbitMQ
        auditClient.log(AuditRequest.builder()
            .userId(userId)
            .entityType("Transaction")
            .entityId(saved.getId())
            .action(AuditRequest.AuditAction.CREATE)
            .newValue(request.toString())
            .build(), tenantId);

        log.info("Logged {} transaction for investment: {} by user: {} in tenant: {}", 
            saved.getTransactionType(), saved.getInvestmentId(), userId, tenantId);
        return toResponse(saved);
    }

    public TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
            .id(t.getId())
            .tenantId(t.getTenantId())
            .userId(t.getUserId())
            .investmentId(t.getInvestmentId())
            .transactionType(t.getTransactionType())
            .quantity(t.getQuantity())
            .price(t.getPrice())
            .transactionDate(t.getTransactionDate())
            .source(t.getSource())
            .createdAt(t.getCreatedAt())
            .updatedAt(t.getUpdatedAt())
            .build();
    }
}

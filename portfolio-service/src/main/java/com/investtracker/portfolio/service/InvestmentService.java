package com.investtracker.portfolio.service;

import com.investtracker.portfolio.client.AuditClient;
import com.investtracker.portfolio.dto.InvestmentRequest;
import com.investtracker.portfolio.dto.InvestmentResponse;
import com.investtracker.portfolio.dto.audit.AuditRequest;
import com.investtracker.portfolio.entity.Investment;
import com.investtracker.portfolio.entity.Investment.InvestmentType;
import com.investtracker.portfolio.exception.ResourceNotFoundException;
import com.investtracker.portfolio.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * InvestmentService — Refactored for Phase 5 (Event-Driven Auditing).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final AuditClient        auditClient;

    @Transactional(readOnly = true)
    public Page<InvestmentResponse> getInvestments(
        String tenantId,
        Long userId,
        InvestmentType type,
        String search,
        Pageable pageable
    ) {
        return investmentRepository
            .findByTenantAndUser(tenantId, userId, type, search, pageable)
            .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public InvestmentResponse getById(Long id, String tenantId, Long userId) {
        Investment investment = findSecurely(id, tenantId, userId);
        return toResponse(investment);
    }

    @Transactional
    public InvestmentResponse create(InvestmentRequest request, String tenantId, Long userId) {
        Investment investment = Investment.builder()
            .tenantId(tenantId)
            .userId(userId)
            .name(request.getName())
            .type(request.getType())
            .symbol(request.getSymbol())
            .quantity(request.getQuantity())
            .purchasePrice(request.getPurchasePrice())
            .currentPrice(request.getCurrentPrice())
            .purchaseDate(request.getPurchaseDate())
            .notes(request.getNotes())
            .build();

        Investment saved = investmentRepository.save(investment);
        
        // Asynchronous Event logging
        auditClient.log(AuditRequest.builder()
            .userId(userId)
            .entityType("Investment")
            .entityId(saved.getId())
            .action(AuditRequest.AuditAction.CREATE)
            .newValue(request.toString())
            .build(), tenantId);

        log.info("Created investment: {} for user: {} in tenant: {}", saved.getName(), userId, tenantId);
        return toResponse(saved);
    }

    @Transactional
    public InvestmentResponse update(Long id, InvestmentRequest request, String tenantId, Long userId) {
        Investment existing = findSecurely(id, tenantId, userId);
        String oldValue = existing.toString();

        existing.setName(request.getName());
        existing.setType(request.getType());
        existing.setSymbol(request.getSymbol());
        existing.setQuantity(request.getQuantity());
        existing.setPurchasePrice(request.getPurchasePrice());
        existing.setCurrentPrice(request.getCurrentPrice());
        existing.setPurchaseDate(request.getPurchaseDate());
        existing.setNotes(request.getNotes());

        Investment saved = investmentRepository.save(existing);

        // Asynchronous Event logging
        auditClient.log(AuditRequest.builder()
            .userId(userId)
            .entityType("Investment")
            .entityId(saved.getId())
            .action(AuditRequest.AuditAction.UPDATE)
            .oldValue(oldValue)
            .newValue(request.toString())
            .build(), tenantId);

        log.info("Updated investment: {} for user: {} in tenant: {}", saved.getId(), userId, tenantId);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id, String tenantId, Long userId) {
        Investment investment = findSecurely(id, tenantId, userId);
        investment.softDelete();
        investmentRepository.save(investment);

        // Asynchronous Event logging
        auditClient.log(AuditRequest.builder()
            .userId(userId)
            .entityType("Investment")
            .entityId(id)
            .action(AuditRequest.AuditAction.DELETE)
            .build(), tenantId);

        log.info("Soft deleted investment: {} for user: {} in tenant: {}", id, userId, tenantId);
    }

    public InvestmentResponse toResponse(Investment i) {
        return InvestmentResponse.builder()
            .id(i.getId())
            .tenantId(i.getTenantId())
            .userId(i.getUserId())
            .name(i.getName())
            .type(i.getType())
            .symbol(i.getSymbol())
            .quantity(i.getQuantity())
            .purchasePrice(i.getPurchasePrice())
            .currentPrice(i.getCurrentPrice())
            .investedAmount(i.getInvestedAmount())
            .currentValue(i.getCurrentValue())
            .profitLoss(i.getProfitLoss())
            .profitLossPct(i.getProfitLossPct())
            .purchaseDate(i.getPurchaseDate())
            .notes(i.getNotes())
            .createdAt(i.getCreatedAt())
            .updatedAt(i.getUpdatedAt())
            .build();
    }

    private Investment findSecurely(Long id, String tenantId, Long userId) {
        return investmentRepository.findByIdAndTenantIdAndUserId(id, tenantId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Investment", id));
    }
}

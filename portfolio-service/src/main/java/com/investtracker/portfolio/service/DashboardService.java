package com.investtracker.portfolio.service;

import com.investtracker.portfolio.dto.DashboardResponse;
import com.investtracker.portfolio.dto.InvestmentResponse;
import com.investtracker.portfolio.entity.Investment.InvestmentType;
import com.investtracker.portfolio.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InvestmentRepository investmentRepository;
    private final InvestmentService    investmentService;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String tenantId, Long userId) {

        List<InvestmentResponse> all = investmentRepository
            .findByTenantAndUser(tenantId, userId, null, null, PageRequest.of(0, Integer.MAX_VALUE))
            .stream()
            .map(investmentService::toResponse)
            .toList();

        BigDecimal totalInvested = all.stream()
            .map(InvestmentResponse::getInvestedAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentValue = all.stream()
            .map(InvestmentResponse::getCurrentValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profitLoss = currentValue.subtract(totalInvested);

        BigDecimal profitLossPct = totalInvested.compareTo(BigDecimal.ZERO) == 0
            ? BigDecimal.ZERO
            : profitLoss.divide(totalInvested, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        Map<InvestmentType, BigDecimal> byType = all.stream()
            .collect(Collectors.groupingBy(
                InvestmentResponse::getType,
                Collectors.reducing(BigDecimal.ZERO, InvestmentResponse::getCurrentValue, BigDecimal::add)
            ));

        List<DashboardResponse.AllocationItem> allocation = byType.entrySet().stream()
            .map(entry -> {
                double pct = currentValue.compareTo(BigDecimal.ZERO) == 0
                    ? 0.0
                    : entry.getValue().doubleValue() / currentValue.doubleValue() * 100.0;
                return DashboardResponse.AllocationItem.builder()
                    .type(entry.getKey().name())
                    .value(entry.getValue())
                    .percentage(Math.round(pct * 100.0) / 100.0)
                    .build();
            })
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .collect(Collectors.toList());

        List<InvestmentResponse> topPerformers = investmentRepository
            .findTopPerformers(tenantId, userId, PageRequest.of(0, 5))
            .stream()
            .map(investmentService::toResponse)
            .toList();

        List<DashboardResponse.GrowthPoint> growth = buildGrowthTimeline(all);

        return DashboardResponse.builder()
            .totalInvested(totalInvested.setScale(2, RoundingMode.HALF_UP))
            .currentValue(currentValue.setScale(2, RoundingMode.HALF_UP))
            .profitLoss(profitLoss.setScale(2, RoundingMode.HALF_UP))
            .profitLossPct(profitLossPct.setScale(2, RoundingMode.HALF_UP))
            .investmentCount((long) all.size())
            .allocation(allocation)
            .growth(growth)
            .topPerformers(topPerformers)
            .build();
    }

    private List<DashboardResponse.GrowthPoint> buildGrowthTimeline(List<InvestmentResponse> all) {
        return all.stream()
            .sorted((a, b) -> a.getPurchaseDate().compareTo(b.getPurchaseDate()))
            .collect(Collectors.groupingBy(
                i -> i.getPurchaseDate().withDayOfMonth(1).toString(),
                Collectors.reducing(BigDecimal.ZERO, InvestmentResponse::getCurrentValue, BigDecimal::add)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> DashboardResponse.GrowthPoint.builder()
                .date(e.getKey())
                .value(e.getValue().setScale(2, RoundingMode.HALF_UP))
                .build())
            .collect(Collectors.toList());
    }
}

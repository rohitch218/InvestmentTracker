package com.investtracker.portfolio.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal profitLoss;
    private BigDecimal profitLossPct;
    private Long       investmentCount;

    private List<AllocationItem> allocation;
    private List<GrowthPoint> growth;
    private List<InvestmentResponse> topPerformers;

    @Data
    @Builder
    public static class AllocationItem {
        private String     type;
        private BigDecimal value;
        private Double     percentage;
    }

    @Data
    @Builder
    public static class GrowthPoint {
        private String     date;
        private BigDecimal value;
    }
}

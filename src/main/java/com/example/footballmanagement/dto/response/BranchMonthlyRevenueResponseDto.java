package com.example.footballmanagement.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchMonthlyRevenueResponseDto {

    private Integer year; // năm đang xem
    private BigDecimal totalNetRevenue; // tổng doanh thu ròng của cả năm

    private List<MonthlyRevenueItem> monthlyRevenues; // danh sách doanh thu từng tháng

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyRevenueItem {
        private Integer month; // tháng (1–12)
        private BigDecimal approvedRevenue; // tổng tiền đã duyệt
        private BigDecimal cancelledOrRefunded; // tổng tiền hủy hoặc hoàn
        private BigDecimal netRevenue; // doanh thu ròng (approved - cancelled/refund)
    }
}

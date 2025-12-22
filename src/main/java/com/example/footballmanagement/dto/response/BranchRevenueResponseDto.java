package com.example.footballmanagement.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchRevenueResponseDto {

    /** Tổng doanh thu thuần (đã trừ các khoản hủy/refund) */
    private BigDecimal netRevenue;

    /** Tổng doanh thu từ các booking được duyệt (approved) */
    private BigDecimal approvedRevenue;

    /** Tổng tiền bị hủy hoặc hoàn */
    private BigDecimal cancelledOrRefundedAmount;

    /** 
     * Danh sách chi tiết lý do hình thành doanh thu (ví dụ cho tooltip chart)
     * Mỗi item có tên loại, số tiền, tỷ lệ %
     */
    private List<RevenueDetailItem> details;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueDetailItem {
        private String label;       // "Đã duyệt", "Đã hủy/Hoàn tiền"
        private BigDecimal amount;  // số tiền
        private double percentage;  // % trên tổng
    }
}

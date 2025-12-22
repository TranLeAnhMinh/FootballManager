package com.example.footballmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchMonthlyRevenueRequestDto {
    /**
     * Năm cần xem doanh thu (ví dụ: 2025)
     * Nếu không truyền thì service sẽ mặc định lấy năm hiện tại.
     */
    private Integer year;
}

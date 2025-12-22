package com.example.footballmanagement.dto.request;

import java.time.LocalDate;

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
public class BranchRevenueRequestDto {

    /** 
     * Ngày cần xem doanh thu.
     * Nếu null thì backend mặc định là LocalDate.now() (hôm nay)
     */
    private LocalDate date;
}

package com.example.footballmanagement.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingListResponse {
    /** Trang hiện tại (0-based) */
    private int page;

    /** Kích thước trang */
    private int size;

    /** Tổng số phần tử */
    private long totalElements;

    /** Tổng số trang */
    private int totalPages;

    /** Danh sách các đơn đặt sân */
    private List<BookingListItemResponse> items;
}

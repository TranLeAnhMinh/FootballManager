package com.example.footballmanagement.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class BranchBookingListResponse {
    private int totalItems;
    private int totalPages;
    private int currentPage;
    private List<BranchBookingResponse> bookings;
}

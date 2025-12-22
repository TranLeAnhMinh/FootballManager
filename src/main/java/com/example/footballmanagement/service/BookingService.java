package com.example.footballmanagement.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.footballmanagement.dto.request.BookingRequest;
import com.example.footballmanagement.dto.response.BookingHistoryResponse;
import com.example.footballmanagement.dto.response.BookingResponse;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, UUID user);
    BookingResponse getBookingResponseById(UUID bookingId);
    Page<BookingHistoryResponse> getBookingHistory(UUID userId, Pageable pageable);
}

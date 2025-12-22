package com.example.footballmanagement.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.example.footballmanagement.dto.request.BookingSlotRequest;
import com.example.footballmanagement.dto.response.BookingSlotResponse;
import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.entity.BookingSlot;

public interface BookingSlotService {
    
    // ✅ Tạo nhiều slot khi user đặt booking
    List<BookingSlot> createSlots(List<BookingSlotRequest> requests, Booking booking);

    // ✅ Check overlap khi tạo booking
    boolean existsOverlap(UUID pitchId, OffsetDateTime startAt, OffsetDateTime endAt);

    // ✅ Lấy toàn bộ slot của 1 pitch (FE load calendar mặc định)
    List<BookingSlotResponse> getByPitch(UUID pitchId);

    // ✅ Lấy slot theo khoảng thời gian (ví dụ FE load theo tháng)
    List<BookingSlotResponse> getByPitchAndRange(UUID pitchId, OffsetDateTime from, OffsetDateTime to);

    // ✅ Xoá tất cả slot theo booking
    void deleteByBookingId(UUID bookingId);
}

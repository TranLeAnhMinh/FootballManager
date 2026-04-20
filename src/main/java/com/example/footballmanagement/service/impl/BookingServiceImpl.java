package com.example.footballmanagement.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.dto.request.BookingRequest;
import com.example.footballmanagement.dto.response.BookingHistoryResponse;
import com.example.footballmanagement.dto.response.BookingPriceResponse;
import com.example.footballmanagement.dto.response.BookingResponse;
import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.entity.BookingSlot;
import com.example.footballmanagement.entity.Pitch;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.enums.BookingStatus;
import com.example.footballmanagement.repository.BookingRepository;
import com.example.footballmanagement.repository.UserRepository;
import com.example.footballmanagement.service.BookingService;
import com.example.footballmanagement.service.BookingSlotService;
import com.example.footballmanagement.service.PitchService;
import com.example.footballmanagement.service.PricingService;
import com.example.footballmanagement.utils.ConverterUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final BookingSlotService slotService;
    private final PitchService pitchService;
    private final PricingService pricingService;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pitch pitch = pitchService.getPitchEntity(request.getPitchId());

        Booking booking = Booking.builder()
                .user(user)
                .pitch(pitch)
                .branch(pitch.getBranch())
                .note(request.getNote())
                .status(BookingStatus.PENDING)
                .build();

        List<BookingSlot> slots = slotService.createSlots(request.getSlots(), booking);
        booking.setSlots(slots);

        var pricing = pricingService.calculatePrice(
                pitch.getId(),
                request.getSlots(),
                request.getVoucherCode(),
                user.getId()
        );

        booking.setFinalPrice(pricing.getFinalPrice());

        bookingRepo.save(booking);

        return ConverterUtil.toBookingResponse(booking, pricing);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingResponseById(UUID bookingId) {
        Booking booking = bookingRepo.findBookingDetailById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        var pricing = BookingPriceResponse.builder()
                .basePrice(null)
                .voucherDiscount(null)
                .finalPrice(booking.getFinalPrice())
                .currency("VND")
                .build();

        return ConverterUtil.toBookingResponse(booking, pricing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingHistoryResponse> getBookingHistory(UUID userId, Pageable pageable) {
        List<Booking> allBookings = bookingRepo.findBookingsForHistoryByUser(userId);

        int start = (int) pageable.getOffset();
        int total = allBookings.size();

        if (start >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        int end = Math.min(start + pageable.getPageSize(), total);

        List<BookingHistoryResponse> content = allBookings.subList(start, end)
                .stream()
                .map(ConverterUtil::toBookingHistoryResponse)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }
}
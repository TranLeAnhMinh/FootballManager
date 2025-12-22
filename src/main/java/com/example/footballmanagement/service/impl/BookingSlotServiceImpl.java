package com.example.footballmanagement.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.dto.request.BookingSlotRequest;
import com.example.footballmanagement.dto.response.BookingSlotResponse;
import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.entity.BookingSlot;
import com.example.footballmanagement.repository.BookingSlotRepository;
import com.example.footballmanagement.service.BookingSlotService;
import com.example.footballmanagement.service.MaintenanceWindowService;
import com.example.footballmanagement.utils.ConverterUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingSlotServiceImpl implements BookingSlotService {

    private final BookingSlotRepository slotRepo;
    private final MaintenanceWindowService maintenanceWindowService;

    // ✅ generate full danh sách allowed times trong 1 ngày
    private static final List<String> ALLOWED_TIMES = List.of(
        "00:00", "00:45", "01:30", "02:15", "03:00", "03:45",
        "04:30", "05:15", "06:00", "06:45", "07:30", "08:15",
        "09:00", "09:45", "10:30", "11:15", "12:00", "12:45",
        "13:30", "14:15", "15:00", "15:45", "16:30", "17:15",
        "18:00", "18:45", "19:30", "20:15", "21:00", "21:45",
        "22:30", "23:15"
    );

    @Override
    @Transactional
    public List<BookingSlot> createSlots(List<BookingSlotRequest> requests, Booking booking) {
        UUID pitchId = booking.getPitch().getId();

        List<BookingSlot> slots = requests.stream()
                .map(req -> {
                    if (req.getStartAt().isAfter(req.getEndAt())) {
                        throw new IllegalArgumentException("Start time must be before end time");
                    }

                String startStr = req.getStartAt().toLocalTime().withSecond(0).withNano(0).toString();
                String endStr   = req.getEndAt().toLocalTime().withSecond(0).withNano(0).toString();

                    if (!ALLOWED_TIMES.contains(startStr) || !ALLOWED_TIMES.contains(endStr)) {
                        throw new IllegalArgumentException("Invalid slot time. Allowed times: " + ALLOWED_TIMES);
                    }

                    // ✅ check overlap (PENDING, APPROVED)
                    if (existsOverlap(pitchId, req.getStartAt(), req.getEndAt())) {
                        throw new IllegalArgumentException("Slot overlaps with another booking");
                    }

                    // ✅ check maintenance
                    if (maintenanceWindowService.existsOverlap(pitchId, req.getStartAt(), req.getEndAt())) {
                        throw new IllegalArgumentException("Pitch is under maintenance in this period");
                    }

                    return BookingSlot.builder()
                        .booking(booking)
                        .pitch(booking.getPitch())
                        .startAt(req.getStartAt())
                        .endAt(req.getEndAt())
                        .build();
                })
                .toList();

        return slots;   
    }
    @Override
    public boolean existsOverlap(UUID pitchId, OffsetDateTime startAt, OffsetDateTime endAt) {
        return slotRepo.existsOverlap(pitchId, startAt, endAt);
    }

    // ✅ Lấy toàn bộ slot cho calendar
    @Override
    public List<BookingSlotResponse> getByPitch(UUID pitchId) {
        return slotRepo.findApprovedByPitch_Id(pitchId)
                .stream()
                .map(ConverterUtil::toBookingSlotResponse)
                .toList();
    }

    // ✅ Lấy slot theo khoảng thời gian
    @Override
    public List<BookingSlotResponse> getByPitchAndRange(UUID pitchId, OffsetDateTime from, OffsetDateTime to) {
        return slotRepo.findApprovedByPitch_IdAndRange(pitchId, from, to)
                .stream()
                .map(ConverterUtil::toBookingSlotResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByBookingId(UUID bookingId) {
        slotRepo.deleteByBookingId(bookingId);
    }
}

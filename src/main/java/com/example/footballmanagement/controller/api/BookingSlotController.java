package com.example.footballmanagement.controller.api;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.dto.response.BookingSlotResponse;
import com.example.footballmanagement.service.BookingSlotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pitches/{pitchId}/booking-slots")
@RequiredArgsConstructor
public class BookingSlotController {

    private final BookingSlotService bookingSlotService;

    /**
     * ✅ Lấy toàn bộ booking slots của pitch (hoặc filter theo khoảng thời gian)
     */
    @GetMapping
    public ResponseEntity<List<BookingSlotResponse>> getAllByPitch(
            @PathVariable UUID pitchId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        if (from != null && to != null) {
            return ResponseEntity.ok(bookingSlotService.getByPitchAndRange(pitchId, from, to));
        }
        return ResponseEntity.ok(bookingSlotService.getByPitch(pitchId));
    }
}

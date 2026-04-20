package com.example.footballmanagement.service.impl;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.dto.request.BranchBookingFilterRequest;
import com.example.footballmanagement.dto.request.UpdateBookingStatusRequest;
import com.example.footballmanagement.dto.response.BranchBookingResponse;
import com.example.footballmanagement.dto.response.UpdateBookingStatusResponse;
import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.entity.Branch;
import com.example.footballmanagement.entity.enums.BookingStatus;
import com.example.footballmanagement.repository.BookingRepository;
import com.example.footballmanagement.repository.BranchRepository;
import com.example.footballmanagement.service.BranchBookingService;
import com.example.footballmanagement.service.EmailTemplateService;
import com.example.footballmanagement.utils.ConverterUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchBookingServiceImpl implements BranchBookingService {

    private final BookingRepository bookingRepo;
    private final BranchRepository branchRepo;
    private final EmailTemplateService emailTemplateService;

    @Override
    @Transactional(readOnly = true)
    public Page<BranchBookingResponse> getBookingsOfAdminBranch(UUID adminId,
                                                                BranchBookingFilterRequest filter,
                                                                Pageable pageable) {
        Branch branch = branchRepo.findByAdmin_Id(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin is not managing any branch"));
        UUID branchId = branch.getId();

        boolean hasStartDate = filter.getStartDate() != null;
        boolean hasEndDate = filter.getEndDate() != null;

        OffsetDateTime safeStartDate = hasStartDate
                ? filter.getStartDate()
                : OffsetDateTime.parse("1970-01-01T00:00:00+00:00");

        OffsetDateTime safeEndDate = hasEndDate
                ? filter.getEndDate()
                : OffsetDateTime.parse("9999-12-31T23:59:59+00:00");

        Page<UUID> bookingIdPage = bookingRepo.findBranchBookingIds(
                branchId,
                filter.getStatus(),
                filter.getPitchName(),
                filter.getUserKeyword(),
                hasStartDate,
                safeStartDate,
                hasEndDate,
                safeEndDate,
                pageable
        );

        if (bookingIdPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<UUID> ids = bookingIdPage.getContent();
        List<Booking> bookings = bookingRepo.findBranchBookingsWithDetailsByIds(ids);

        Map<UUID, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            orderMap.put(ids.get(i), i);
        }

        bookings.sort(Comparator.comparingInt(b -> orderMap.getOrDefault(b.getId(), Integer.MAX_VALUE)));

        List<BranchBookingResponse> content = bookings.stream()
                .map(ConverterUtil::toBranchBookingResponse)
                .toList();

        return new PageImpl<>(content, pageable, bookingIdPage.getTotalElements());
    }

    @Override
    @Transactional
    public UpdateBookingStatusResponse updateBookingStatus(UUID adminId, UpdateBookingStatusRequest request) {
        log.info("🔄 Admin {} yêu cầu cập nhật trạng thái booking {}", adminId, request.getBookingId());

        Branch branch = branchRepo.findByAdmin_Id(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin is not managing any branch"));

        Booking booking = bookingRepo.findById(UUID.fromString(request.getBookingId()))
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getBranch().getId().equals(branch.getId())) {
            throw new IllegalStateException("Booking does not belong to your branch");
        }

        BookingStatus oldStatus = booking.getStatus();
        BookingStatus newStatus = request.getNewStatus();

        boolean validTransition =
                (oldStatus == BookingStatus.APPROVED && newStatus == BookingStatus.WAITING_REFUND) ||
                (oldStatus == BookingStatus.WAITING_REFUND && newStatus == BookingStatus.REFUNDED);

        if (!validTransition) {
            throw new IllegalStateException("Invalid status transition: " + oldStatus + " → " + newStatus);
        }

        booking.setStatus(newStatus);
        bookingRepo.save(booking);

        try {
            if (newStatus == BookingStatus.WAITING_REFUND) {
                emailTemplateService.sendWaitingRefundNotice(booking, request.getAdminNote());
            } else if (newStatus == BookingStatus.REFUNDED) {
                emailTemplateService.sendRefundedNotice(booking, request.getAdminNote());
            }
        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi mail trạng thái {} cho {}: {}", newStatus, booking.getUser().getEmail(), e.getMessage());
        }

        return UpdateBookingStatusResponse.builder()
                .bookingId(booking.getId().toString())
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .adminNote(request.getAdminNote())
                .message("Booking status updated and notification sent successfully.")
                .build();
    }
}
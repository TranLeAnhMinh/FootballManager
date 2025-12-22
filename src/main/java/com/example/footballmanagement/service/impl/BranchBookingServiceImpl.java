package com.example.footballmanagement.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
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

    // ============================================================
    // ✅ Lấy danh sách booking của chi nhánh
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public Page<BranchBookingResponse> getBookingsOfAdminBranch(UUID adminId,
                                                                BranchBookingFilterRequest filter,
                                                                Pageable pageable) {
        Branch branch = branchRepo.findByAdmin_Id(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin is not managing any branch"));
        UUID branchId = branch.getId();

        Page<Booking> bookings = bookingRepo.findBranchBookings(
                branchId,
                filter.getStatus(),
                filter.getPitchName(),
                filter.getUserKeyword(),
                filter.getStartDate(),
                filter.getEndDate(),
                pageable
        );

        return bookings.map(ConverterUtil::toBranchBookingResponse);
    }

// ============================================================
// ✅ Cập nhật trạng thái booking
// ============================================================
@Override
@Transactional
public UpdateBookingStatusResponse updateBookingStatus(UUID adminId, UpdateBookingStatusRequest request) {
    log.info("🔄 Admin {} yêu cầu cập nhật trạng thái booking {}", adminId, request.getBookingId());

    // 1️⃣ Xác minh chi nhánh của admin
    Branch branch = branchRepo.findByAdmin_Id(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Admin is not managing any branch"));

    // 2️⃣ Tìm booking
    Booking booking = bookingRepo.findById(UUID.fromString(request.getBookingId()))
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

    // 3️⃣ Kiểm tra booking có thuộc chi nhánh admin không
    if (!booking.getBranch().getId().equals(branch.getId())) {
        throw new IllegalStateException("Booking does not belong to your branch");
    }

    // 4️⃣ Kiểm tra trạng thái hợp lệ
    BookingStatus oldStatus = booking.getStatus();
    BookingStatus newStatus = request.getNewStatus();

    boolean validTransition =
            (oldStatus == BookingStatus.APPROVED && newStatus == BookingStatus.WAITING_REFUND) ||
            (oldStatus == BookingStatus.WAITING_REFUND && newStatus == BookingStatus.REFUNDED);

    if (!validTransition) {
        throw new IllegalStateException("Invalid status transition: " + oldStatus + " → " + newStatus);
    }

    // 5️⃣ Cập nhật trạng thái (❌ KHÔNG ghi đè note của khách)
    booking.setStatus(newStatus);
    bookingRepo.save(booking);

    // 6️⃣ Gửi mail phù hợp (dùng adminNote)
    try {
        if (newStatus == BookingStatus.WAITING_REFUND) {
            emailTemplateService.sendWaitingRefundNotice(booking, request.getAdminNote());
        } else if (newStatus == BookingStatus.REFUNDED) {
            emailTemplateService.sendRefundedNotice(booking, request.getAdminNote());
        }
    } catch (Exception e) {
        log.error("❌ Lỗi khi gửi mail trạng thái {} cho {}: {}", newStatus, booking.getUser().getEmail(), e.getMessage());
    }

    // 7️⃣ Trả response
    return UpdateBookingStatusResponse.builder()
            .bookingId(booking.getId().toString())
            .oldStatus(oldStatus)
            .newStatus(newStatus)
            .adminNote(request.getAdminNote())
            .message("Booking status updated and notification sent successfully.")
            .build();
}
}

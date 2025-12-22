package com.example.footballmanagement.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.example.footballmanagement.dto.request.ReviewRequest;
import com.example.footballmanagement.dto.response.BookingHistoryResponse;
import com.example.footballmanagement.dto.response.BookingPriceResponse;
import com.example.footballmanagement.dto.response.BookingResponse;
import com.example.footballmanagement.dto.response.BookingSlotResponse;
import com.example.footballmanagement.dto.response.BranchBookingResponse;
import com.example.footballmanagement.dto.response.MaintenanceWindowFilterResponse;
import com.example.footballmanagement.dto.response.MaintenanceWindowResponse;
import com.example.footballmanagement.dto.response.PitchDetailResponse;
import com.example.footballmanagement.dto.response.PitchImageResponse;
import com.example.footballmanagement.dto.response.PitchResponseDto;
import com.example.footballmanagement.dto.response.PitchTypeBranchesResponse;
import com.example.footballmanagement.dto.response.ReviewResponse;
import com.example.footballmanagement.dto.response.UserUpdateResponse;
import com.example.footballmanagement.dto.response.VoucherResponse;
import com.example.footballmanagement.dto.response.VoucherUsageResponse;
import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.entity.BookingSlot;
import com.example.footballmanagement.entity.Branch;
import com.example.footballmanagement.entity.MaintenanceWindow;
import com.example.footballmanagement.entity.Pitch;
import com.example.footballmanagement.entity.PitchImage;
import com.example.footballmanagement.entity.Review;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.Voucher;

public class ConverterUtil {
    // ========== User ==========
    public static UserUpdateResponse toUserUpdateResponse(User user) {
        return UserUpdateResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

    // ========== Pitch ==========
    public static PitchTypeBranchesResponse.PitchSummaryDTO toPitchSummaryDTO(Pitch pitch) {
        return PitchTypeBranchesResponse.PitchSummaryDTO.builder()
                .id(pitch.getId())
                .name(pitch.getName())
                .active(pitch.isActive())
                .build();
    }

    public static PitchTypeBranchesResponse.BranchSummaryDTO toBranchSummaryDTO(Branch branch, List<Pitch> pitches) {
        return PitchTypeBranchesResponse.BranchSummaryDTO.builder()
                .id(branch.getId())
                .name(branch.getName())
                .pitches(pitches.stream()
                        .map(ConverterUtil::toPitchSummaryDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    public static PitchDetailResponse toPitchDetailResponse(Pitch pitch) {
        return PitchDetailResponse.builder()
                .id(pitch.getId())
                .name(pitch.getName())
                .location(pitch.getLocation())
                .description(pitch.getDescription())
                .active(pitch.isActive())
                .branchId(pitch.getBranch().getId())
                .branchName(pitch.getBranch().getName())
                .pitchTypeId(pitch.getPitchType().getId())
                .pitchTypeName(pitch.getPitchType().getName())
                .build();
    }

    public static PitchImageResponse toPitchImageResponse(PitchImage img) {
        return PitchImageResponse.builder()
                .id(img.getId())
                .url(img.getUrl())
                .isCover(img.isCover())
                .build();
    }

    // ========== Review ==========
    public static ReviewResponse toReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userFullName(review.getUser().getFullName())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static Review fromReviewRequest(Pitch pitch, User user, ReviewRequest request) {
        return Review.builder()
                .pitch(pitch)
                .user(user)
                .rating(request.getRating())
                .content(request.getContent())
                .build();
    }

    // ========== Booking ==========
    public static BookingResponse toBookingResponse(Booking booking, BookingPriceResponse pricing) {
        List<BookingSlotResponse> slotResponses = booking.getSlots().stream()
                .map(ConverterUtil::toBookingSlotResponse)
                .toList();

        VoucherUsageResponse usageResponse = null;
        if (booking.getVoucherUsage() != null) {
            var usage = booking.getVoucherUsage();
            usageResponse = VoucherUsageResponse.builder()
                    .voucherId(usage.getVoucher().getId())
                    .code(usage.getVoucher().getCode())
                    .type(usage.getVoucher().getType().name())
                    .discountAmount(usage.getDiscountAmount())
                    .usedAt(usage.getUsedAt())
                    .build();
        }

        return BookingResponse.builder()
                .id(booking.getId())
                .pitchName(booking.getPitch() != null ? booking.getPitch().getName() : null)
                .branchName(booking.getBranch() != null ? booking.getBranch().getName() : null)
                .userName(booking.getUser() != null ? booking.getUser().getFullName() : null)
                .status(booking.getStatus())
                .note(booking.getNote())
                .bookingDate(
                        booking.getSlots().isEmpty()
                                ? null
                                : booking.getSlots().get(0).getStartAt().toLocalDate().toString()
                )
                .slots(slotResponses)
                .voucherUsage(usageResponse)
                .pricing(pricing)
                .build();
    }

    public static BookingSlotResponse toBookingSlotResponse(BookingSlot slot) {
        return BookingSlotResponse.builder()
                .id(slot.getId())
                .startAt(slot.getStartAt())
                .endAt(slot.getEndAt())
                .checkedIn(slot.getCheckedInAt() != null)
                .build();
    }

    // ========== Booking History ==========
    public static BookingHistoryResponse toBookingHistoryResponse(Booking booking) {
        return BookingHistoryResponse.builder()
                .bookingId(booking.getId())
                .pitchName(booking.getPitch().getName())
                .branchName(booking.getBranch().getName())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .finalPrice(booking.getFinalPrice())
                .slots(booking.getSlots().stream()
                        .map(ConverterUtil::toSlotResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public static BookingHistoryResponse.SlotResponse toSlotResponse(BookingSlot slot) {
        return BookingHistoryResponse.SlotResponse.builder()
                .startAt(slot.getStartAt())
                .endAt(slot.getEndAt())
                .checkedIn(slot.getCheckedInAt() != null)
                .build();
    }

    // ========== MaintenanceWindow ==========
    public static MaintenanceWindowResponse toMaintenanceWindowResponse(MaintenanceWindow mw) {
        return MaintenanceWindowResponse.builder()
                .id(mw.getId())
                .reason(mw.getReason())
                .startAt(mw.getStartAt())
                .endAt(mw.getEndAt())
                .build();
    }

    public static VoucherResponse toVoucherResponse(Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .type(voucher.getType())
                .value(voucher.getValue())
                .maxDiscount(voucher.getMaxDiscount())
                .minOrder(voucher.getMinOrder())
                .startAt(voucher.getStartAt())
                .endAt(voucher.getEndAt())
                .active(voucher.isActive())
                .build();
    }
    public static BranchBookingResponse toBranchBookingResponse(Booking booking) {
    var dto = new BranchBookingResponse();
    dto.setBookingId(booking.getId());
    dto.setUserId(booking.getUser().getId());
    dto.setUserFullName(booking.getUser().getFullName());
    dto.setUserEmail(booking.getUser().getEmail());
    dto.setUserPhone(booking.getUser().getPhone());
    dto.setPitchId(booking.getPitch().getId());
    dto.setPitchName(booking.getPitch().getName());
    dto.setPitchType(booking.getPitch().getPitchType().getName());
    dto.setPitchLocation(booking.getPitch().getLocation());
    dto.setStatus(booking.getStatus().name());
    dto.setFinalPrice(booking.getFinalPrice());
    dto.setNote(booking.getNote());
    dto.setCreatedAt(booking.getCreatedAt());

    // Nếu có slot → lấy slot đầu tiên
    if (booking.getSlots() != null && !booking.getSlots().isEmpty()) {
        var slot = booking.getSlots().get(0);
        dto.setStartAt(slot.getStartAt());
        dto.setEndAt(slot.getEndAt());
    }

    return dto;
}
        public static MaintenanceWindowFilterResponse toMaintenanceWindowFilterResponse(MaintenanceWindow m) {
    MaintenanceWindowFilterResponse dto = new MaintenanceWindowFilterResponse();
    dto.setId(m.getId());
    dto.setPitchId(m.getPitch().getId());
    dto.setPitchName(m.getPitch().getName());
    dto.setPitchLocation(m.getPitch().getLocation());
    dto.setPitchTypeName(m.getPitch().getPitchType() != null ? m.getPitch().getPitchType().getName() : null);
    dto.setReason(m.getReason());
    dto.setStartAt(m.getStartAt());
    dto.setEndAt(m.getEndAt());
    dto.setCreatedAt(m.getCreatedAt());
    return dto;
}
 public static PitchResponseDto toPitchResponseDto(Pitch p) {
        if (p == null) return null;

        return PitchResponseDto.builder()
                .id(p.getId())
                .name(p.getName())
                .location(p.getLocation())
                .description(p.getDescription())
                .active(p.isActive())
                .branchId(p.getBranch().getId())
                .branchName(p.getBranch().getName())
                .branchLocation(p.getBranch().getLocation())
                .pitchTypeId(p.getPitchType().getId())
                .pitchTypeName(p.getPitchType().getName())
                .build();
    }

}

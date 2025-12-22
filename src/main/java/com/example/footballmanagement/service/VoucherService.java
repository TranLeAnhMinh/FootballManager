package com.example.footballmanagement.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.footballmanagement.dto.response.VoucherResponse;
import com.example.footballmanagement.entity.Voucher;

public interface VoucherService {

    // ✅ Tìm voucher active theo code (ví dụ khi user nhập voucher)
    Optional<Voucher> findActiveByCode(String code);

    // ✅ Validate voucher có hợp lệ cho user hay không
    void validateVoucher(Voucher voucher, UUID userId, BigDecimal orderAmount, OffsetDateTime bookingDate);

    // ✅ Lấy danh sách voucher khả dụng cho 1 user (dựa vào token -> userId)
    List<VoucherResponse> getAvailableVouchersForUser(UUID userId);
}

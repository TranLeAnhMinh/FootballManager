package com.example.footballmanagement.service;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.Voucher;
import com.example.footballmanagement.entity.VoucherUsage;

public interface VoucherUsageService {

    // ✅ Tạo usage khi user sử dụng voucher thành công
    VoucherUsage createUsage(Voucher voucher, User user, Booking booking, BigDecimal discountAmount);

    // ✅ Đếm số lần user đã dùng voucher này (check perUserLimit)
    long countUsageByUser(UUID userId, UUID voucherId);

}

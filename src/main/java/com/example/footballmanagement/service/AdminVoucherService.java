package com.example.footballmanagement.service;

import java.util.List;
import java.util.UUID;

import com.example.footballmanagement.entity.Voucher;

public interface AdminVoucherService {

    // ✅ Chỉ ADMIN_SYSTEM được tạo voucher
    Voucher createVoucher(Voucher voucher, UUID currentUserId);
    // 🔹 Xóa mềm voucher (ADMIN_SYSTEM)
    void deleteVoucher(UUID voucherId, UUID currentUserId);

    List<Voucher> getAllVouchers(UUID currentUserId);

}

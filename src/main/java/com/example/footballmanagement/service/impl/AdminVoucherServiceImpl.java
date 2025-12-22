package com.example.footballmanagement.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.Voucher;
import com.example.footballmanagement.entity.enums.UserRole;
import com.example.footballmanagement.entity.enums.VoucherType;
import com.example.footballmanagement.exception.ErrorCode;
import com.example.footballmanagement.exception.custom.VoucherException;
import com.example.footballmanagement.repository.UserRepository;
import com.example.footballmanagement.repository.VoucherRepository;
import com.example.footballmanagement.service.AdminVoucherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminVoucherServiceImpl implements AdminVoucherService {

    private final VoucherRepository voucherRepo;
    private final UserRepository userRepo;

    @Override
    public Voucher createVoucher(Voucher voucher, UUID currentUserId) {

        /* ================= LOAD USER ================= */
        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new VoucherException(ErrorCode.USER_NOT_FOUND));

        /* ================= ROLE CHECK ================= */
        if (currentUser.getRole() != UserRole.ADMIN_SYSTEM) {
            throw new AccessDeniedException("Only ADMIN_SYSTEM can create vouchers");
        }

        /* ================= BUSINESS VALIDATION ================= */

        // ❌ Trùng code
        if (voucherRepo.existsByCode(voucher.getCode())) {
            throw new VoucherException(ErrorCode.VOUCHER_CODE_EXISTS);
        }

        // ❌ Sai thời gian
        if (voucher.getStartAt() != null && voucher.getEndAt() != null
                && voucher.getStartAt().isAfter(voucher.getEndAt())) {
            throw new VoucherException(ErrorCode.VOUCHER_INVALID_TIME);
        }

        // ❌ Percent mà value > 100
        if (voucher.getType() == VoucherType.PERCENT
                && voucher.getValue().intValue() > 100) {
            throw new VoucherException(ErrorCode.VOUCHER_PERCENT_INVALID);
        }

        /* ================= DEFAULT FIELDS ================= */
        voucher.setActive(true);

        return voucherRepo.save(voucher);
    }
    @Override
public void deleteVoucher(UUID voucherId, UUID currentUserId) {

    /* ================= LOAD USER ================= */
    User currentUser = userRepo.findById(currentUserId)
            .orElseThrow(() -> new VoucherException(ErrorCode.USER_NOT_FOUND));

    /* ================= ROLE CHECK ================= */
    if (currentUser.getRole() != UserRole.ADMIN_SYSTEM) {
        throw new AccessDeniedException("Only ADMIN_SYSTEM can delete vouchers");
    }

    /* ================= LOAD VOUCHER ================= */
    Voucher voucher = voucherRepo.findById(voucherId)
            .orElseThrow(() -> new VoucherException(ErrorCode.VOUCHER_NOT_FOUND));

    /* ================= BUSINESS VALIDATION ================= */

    // ❌ Đã inactive rồi
    if (!voucher.isActive()) {
        throw new VoucherException(ErrorCode.VOUCHER_ALREADY_INACTIVE);
    }

    // ⚠️ Nếu sau này có voucher_usage thì check ở đây
    // long usageCount = voucherUsageRepo.countByVoucherId(voucherId);
    // if (usageCount > 0) throw new VoucherException(ErrorCode.VOUCHER_ALREADY_USED);

    /* ================= SOFT DELETE ================= */
    voucher.setActive(false);

    voucherRepo.save(voucher);
}
@Override
@Transactional(readOnly = true)
public List<Voucher> getAllVouchers(UUID currentUserId) {

    /* ================= LOAD USER ================= */
    User currentUser = userRepo.findById(currentUserId)
            .orElseThrow(() -> new VoucherException(ErrorCode.USER_NOT_FOUND));

    /* ================= ROLE CHECK ================= */
    if (currentUser.getRole() != UserRole.ADMIN_SYSTEM) {
        throw new AccessDeniedException("Only ADMIN_SYSTEM can view all vouchers");
    }

    /* ================= QUERY ================= */
    return voucherRepo.findAllByOrderByCreatedAtDesc();
}
}


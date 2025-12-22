package com.example.footballmanagement.service.impl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.footballmanagement.dto.response.VoucherResponse;
import com.example.footballmanagement.entity.Voucher;
import com.example.footballmanagement.exception.ErrorCode;
import com.example.footballmanagement.exception.custom.VoucherException;
import com.example.footballmanagement.repository.VoucherRepository;
import com.example.footballmanagement.service.VoucherService;
import com.example.footballmanagement.service.VoucherUsageService;
import com.example.footballmanagement.utils.ConverterUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepo;
    private final VoucherUsageService usageService; // ✅ thay vì gọi repo trực tiếp

    @Override
    public Optional<Voucher> findActiveByCode(String code) {
        return voucherRepo.findByCodeAndActiveTrue(code)
                .filter(v -> {
                    OffsetDateTime now = OffsetDateTime.now();
                    return (v.getStartAt() == null || !now.isBefore(v.getStartAt()))
                    && (v.getEndAt() == null || !now.isAfter(v.getEndAt()));
            });
    }

    @Override
    public void validateVoucher(Voucher voucher, UUID userId, BigDecimal orderAmount,OffsetDateTime bookingDate) {
       
        if (!voucher.isActive()) {
            throw new VoucherException(ErrorCode.VOUCHER_INACTIVE);
        }

        if (voucher.getStartAt() != null && bookingDate.isBefore(voucher.getStartAt())) {
            throw new VoucherException(ErrorCode.VOUCHER_NOT_STARTED);
        }

        if (voucher.getEndAt() != null && bookingDate.isAfter(voucher.getEndAt())) {
            throw new VoucherException(ErrorCode.VOUCHER_EXPIRED);
        }

        if (voucher.getMinOrder() != null &&
                orderAmount.compareTo(voucher.getMinOrder()) < 0) {
            throw new VoucherException(ErrorCode.VOUCHER_MIN_ORDER);
        }

        if (voucher.getPerUserLimit() != null) {
            long usedCount = usageService.countUsageByUser(userId, voucher.getId()); // ✅ gọi service

            if (usedCount >= voucher.getPerUserLimit()) {
                throw new VoucherException(ErrorCode.VOUCHER_LIMIT_REACHED);
            }
        }
    }

    @Override
    public List<VoucherResponse> getAvailableVouchersForUser(UUID userId) {
        OffsetDateTime now = OffsetDateTime.now();

        return voucherRepo.findAllValidVouchers(now).stream()
                .filter(v -> {
                    if (v.getPerUserLimit() != null) {
                        long usedCount = usageService.countUsageByUser(userId, v.getId()); // ✅ gọi service
                        return usedCount < v.getPerUserLimit();
                    }
                    return true; // không giới hạn -> luôn khả dụng
                })
                .map(ConverterUtil::toVoucherResponse)
                .collect(Collectors.toList());
    }
}

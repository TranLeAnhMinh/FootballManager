package com.example.footballmanagement.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.Voucher;
import com.example.footballmanagement.entity.VoucherUsage;
import com.example.footballmanagement.repository.VoucherUsageRepository;
import com.example.footballmanagement.service.VoucherUsageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherUsageServiceImpl implements VoucherUsageService {

    private final VoucherUsageRepository usageRepo;

    @Override
    public VoucherUsage createUsage(Voucher voucher, User user, Booking booking, BigDecimal discountAmount) {
        VoucherUsage usage = VoucherUsage.builder()
                .voucher(voucher)
                .user(user)
                .booking(booking)
                .discountAmount(discountAmount)
                .build();

        return usageRepo.save(usage);
    }

    @Override
    public long countUsageByUser(UUID userId, UUID voucherId) {
        return usageRepo.countByUser_IdAndVoucher_Id(userId, voucherId);
    }
}

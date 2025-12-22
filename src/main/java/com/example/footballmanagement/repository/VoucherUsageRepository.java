package com.example.footballmanagement.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.VoucherUsage;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, UUID> {
    // ✅ Đếm số lần user đã dùng voucher này (dùng property path)
    long countByUser_IdAndVoucher_Id(UUID userId, UUID voucherId);
  
}

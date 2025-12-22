package com.example.footballmanagement.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.Voucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, UUID> {

    // ✅ Tìm voucher theo code và phải active
    Optional<Voucher> findByCodeAndActiveTrue(String code);

    // ✅ Lấy tất cả voucher active, còn hiệu lực theo thời gian
    @Query("""
        SELECT v
        FROM Voucher v
        WHERE v.active = true
          AND (v.startAt IS NULL OR v.startAt <= :now)
          AND (v.endAt IS NULL OR v.endAt >= :now)
    """)
    List<Voucher> findAllValidVouchers(OffsetDateTime now);
    boolean existsByCode(String code);

    // 🔹 Check tồn tại voucher đang active (phục vụ xóa mềm)
boolean existsByIdAndActiveTrue(UUID id);

// 🔹 Admin xem toàn bộ voucher (kể cả inactive / hết hạn)
List<Voucher> findAllByOrderByCreatedAtDesc();
}

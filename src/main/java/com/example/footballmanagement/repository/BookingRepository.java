package com.example.footballmanagement.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.entity.enums.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    // chỉ dùng save(), findById() → không cần custom gì thêm
    // ✅ Lấy tất cả booking status = PENDING mà đã quá thời gian cutoff
    List<Booking> findAllByStatusAndCreatedAtBefore(
            BookingStatus status,
            OffsetDateTime cutoff
    );

    @Query("""
        SELECT DISTINCT b 
        FROM Booking b
        LEFT JOIN FETCH b.slots s
        LEFT JOIN FETCH b.pitch p
        LEFT JOIN FETCH b.branch br
        WHERE b.user.id = :userId
        ORDER BY b.createdAt DESC
    """)
    Page<Booking> findBookingsWithSlotsByUser(UUID userId, Pageable pageable);
    boolean existsByUser_IdAndPitch_IdAndStatusIn(UUID userId, UUID pitchId, List<BookingStatus> statuses);
    @Query("""
    SELECT DISTINCT b
    FROM Booking b
    JOIN b.pitch p
    JOIN b.branch br
    JOIN b.user u
    LEFT JOIN b.slots s
    WHERE br.id = :branchId
      AND (:status IS NULL OR b.status = :status)
      AND (COALESCE(:pitchName, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:pitchName AS string), '%')))
      AND (COALESCE(:userKeyword, '') = '' OR 
           LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:userKeyword AS string), '%')) OR 
           LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:userKeyword AS string), '%')))
      AND (COALESCE(:startDate, NULL) IS NULL OR s.startAt >= :startDate)
      AND (COALESCE(:endDate, NULL) IS NULL OR s.endAt <= :endDate)
    ORDER BY b.createdAt DESC
""")
Page<Booking> findBranchBookings(
        UUID branchId,
        BookingStatus status,
        String pitchName,
        String userKeyword,
        OffsetDateTime startDate,   
        OffsetDateTime endDate,
        Pageable pageable
);


@Modifying
@Query("""
    UPDATE Booking b
    SET b.status = :newStatus,
        b.note = COALESCE(:note, b.note)
    WHERE b.id = :bookingId
""")
int updateBookingStatus(@Param("bookingId") UUID bookingId,
                        @Param("newStatus") BookingStatus newStatus,
                        @Param("note") String note);

                        
@Query("""
    SELECT 
        COALESCE(SUM(
            CASE 
                WHEN b.status IN (
                    'APPROVED',
                    'CHECKED_IN',
                    'NO_SHOW',
                    'CANCELLED',
                    'REFUNDED',
                    'WAITING_REFUND'
                )
                THEN b.finalPrice ELSE 0 
            END
        ), 0),
        COALESCE(SUM(
            CASE 
                WHEN b.status IN (
                    'CANCELLED',
                    'REFUNDED',
                    'WAITING_REFUND'
                )
                THEN b.finalPrice ELSE 0 
            END
        ), 0)
    FROM Booking b
    WHERE b.branch.id = :branchId
      AND b.createdAt BETWEEN :startOfDay AND :endOfDay
""")
List<Object[]> calculateDailyRevenue(
        @Param("branchId") UUID branchId,
        @Param("startOfDay") OffsetDateTime startOfDay,
        @Param("endOfDay") OffsetDateTime endOfDay
);


@Query("""
    SELECT 
        EXTRACT(MONTH FROM b.createdAt) AS month,
        COALESCE(SUM(
            CASE 
                WHEN b.status IN (
                    'APPROVED',
                    'CHECKED_IN',
                    'NO_SHOW',
                    'CANCELLED',
                    'REFUNDED',
                    'WAITING_REFUND'
                )
                THEN b.finalPrice ELSE 0 
            END
        ), 0) AS approvedRevenue,
        COALESCE(SUM(
            CASE 
                WHEN b.status IN (
                    'CANCELLED',
                    'REFUNDED',
                    'WAITING_REFUND'
                )
                THEN b.finalPrice ELSE 0 
            END
        ), 0) AS cancelledOrRefunded
    FROM Booking b
    WHERE b.branch.id = :branchId
      AND EXTRACT(YEAR FROM b.createdAt) = :year
    GROUP BY EXTRACT(MONTH FROM b.createdAt)
    ORDER BY month
""")
List<Object[]> calculateMonthlyRevenue(
        @Param("branchId") UUID branchId,
        @Param("year") int year
);
/*
 * REVENUE LOGIC (WORKAROUND):
 * - Total Charged  = SUM(finalPrice) của các booking đã từng thu tiền
 *   (APPROVED, CHECKED_IN, NO_SHOW, CANCELLED, REFUNDED, WAITING_REFUND)
 *
 * - Total Refunded = SUM(finalPrice) của các booking đã hoàn tiền
 *   (CANCELLED, REFUNDED, WAITING_REFUND)
 *
 * - Net Revenue    = Charged - Refunded
 *
 * Assumption:
 * - Refund luôn 100% finalPrice
 * - Không có partial refund
 * - Chỉ dùng bảng bookings (không dùng payments)
 */
/**
 * ADMIN SYSTEM
 * Tính doanh thu toàn hệ thống trong 1 ngày (ALL branches)
 *
 * @return Object[0] = totalCharged
 *         Object[1] = totalRefunded
 */
@Query("""
    SELECT 
        COALESCE(SUM(
            CASE 
                WHEN b.status IN (
                    'APPROVED',
                    'CHECKED_IN',
                    'NO_SHOW',
                    'CANCELLED',
                    'REFUNDED',
                    'WAITING_REFUND'
                )
                THEN b.finalPrice ELSE 0 
            END
        ), 0),
        COALESCE(SUM(
            CASE 
                WHEN b.status IN (
                    'CANCELLED',
                    'REFUNDED',
                    'WAITING_REFUND'
                )
                THEN b.finalPrice ELSE 0 
            END
        ), 0)
    FROM Booking b
    WHERE b.createdAt BETWEEN :startOfDay AND :endOfDay
""")
Object[] calculateSystemDailyRevenue(
        @Param("startOfDay") OffsetDateTime startOfDay,
        @Param("endOfDay") OffsetDateTime endOfDay
);

/**
 * ADMIN SYSTEM
 * Tính doanh thu toàn hệ thống theo từng tháng trong 1 năm
 *
 * @return mỗi row:
 *   [0] = month (1-12)
 *   [1] = totalCharged
 *   [2] = totalRefunded
 */
@Query("""
    SELECT 
        EXTRACT(MONTH FROM b.createdAt) AS month,
        COALESCE(SUM(
            CASE 
                WHEN b.status IN (
                    'APPROVED',
                    'CHECKED_IN',
                    'NO_SHOW',
                    'CANCELLED',
                    'REFUNDED',
                    'WAITING_REFUND'
                )
                THEN b.finalPrice ELSE 0 
            END
        ), 0),
        COALESCE(SUM(
            CASE 
                WHEN b.status IN (
                    'CANCELLED',
                    'REFUNDED',
                    'WAITING_REFUND'
                )
                THEN b.finalPrice ELSE 0 
            END
        ), 0)
    FROM Booking b
    WHERE EXTRACT(YEAR FROM b.createdAt) = :year
    GROUP BY EXTRACT(MONTH FROM b.createdAt)
    ORDER BY month
""")
List<Object[]> calculateSystemMonthlyRevenue(
        @Param("year") int year
);

/**
 * ADMIN SYSTEM
 * Thống kê doanh thu theo từng branch trong 1 năm (group by branch + month)
 *
 * @return mỗi row:
 *   [0] = branchId
 *   [1] = branchName
 *   [2] = month (1-12)
 *   [3] = totalCharged
 *   [4] = totalRefunded
 */
@Query("""
    SELECT 
        br.id,
        br.name,
        EXTRACT(MONTH FROM b.createdAt) AS month,
        COALESCE(SUM(
            CASE 
                WHEN b.status IN (
                    'APPROVED',
                    'CHECKED_IN',
                    'NO_SHOW',
                    'CANCELLED',
                    'REFUNDED',
                    'WAITING_REFUND'
                )
                THEN b.finalPrice ELSE 0 
            END
        ), 0),
        COALESCE(SUM(
            CASE 
                WHEN b.status IN (
                    'CANCELLED',
                    'REFUNDED',
                    'WAITING_REFUND'
                )
                THEN b.finalPrice ELSE 0 
            END
        ), 0)
    FROM Booking b
    JOIN b.branch br
    WHERE EXTRACT(YEAR FROM b.createdAt) = :year
    GROUP BY br.id, br.name, EXTRACT(MONTH FROM b.createdAt)
    ORDER BY br.name, month
""")
List<Object[]> calculateRevenueGroupedByBranch(
        @Param("year") int year
);


}
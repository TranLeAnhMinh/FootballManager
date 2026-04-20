package com.example.footballmanagement.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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

    List<Booking> findAllByStatusAndCreatedAtBefore(
            BookingStatus status,
            OffsetDateTime cutoff
    );

    boolean existsByUser_IdAndPitch_IdAndStatusIn(
            UUID userId,
            UUID pitchId,
            List<BookingStatus> statuses
    );

    /**
     * Bước 1: Lấy page booking ID theo filter
     */
    @Query("""
        SELECT b.id
        FROM Booking b
        JOIN b.pitch p
        JOIN b.branch br
        JOIN b.user u
        WHERE br.id = :branchId
          AND (:status IS NULL OR b.status = :status)
          AND (COALESCE(:pitchName, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:pitchName AS string), '%')))
          AND (
                COALESCE(:userKeyword, '') = ''
                OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:userKeyword AS string), '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:userKeyword AS string), '%'))
          )
          AND (
                :hasStartDate = false
                OR EXISTS (
                    SELECT 1
                    FROM BookingSlot s
                    WHERE s.booking = b
                      AND s.startAt >= :startDate
                )
          )
          AND (
                :hasEndDate = false
                OR EXISTS (
                    SELECT 1
                    FROM BookingSlot s
                    WHERE s.booking = b
                      AND s.endAt <= :endDate
                )
          )
        ORDER BY b.createdAt DESC
    """)
    Page<UUID> findBranchBookingIds(
            @Param("branchId") UUID branchId,
            @Param("status") BookingStatus status,
            @Param("pitchName") String pitchName,
            @Param("userKeyword") String userKeyword,
            @Param("hasStartDate") boolean hasStartDate,
            @Param("startDate") OffsetDateTime startDate,
            @Param("hasEndDate") boolean hasEndDate,
            @Param("endDate") OffsetDateTime endDate,
            Pageable pageable
    );

    /**
     * Bước 2: Lấy full detail theo list ID
     */
    @Query("""
    SELECT DISTINCT b
    FROM Booking b
    LEFT JOIN FETCH b.branch br
    LEFT JOIN FETCH br.admin
    LEFT JOIN FETCH b.user u
    LEFT JOIN FETCH b.pitch p
    LEFT JOIN FETCH p.branch pb
    LEFT JOIN FETCH pb.admin
    LEFT JOIN FETCH p.pitchType
    LEFT JOIN FETCH b.voucherUsage vu
    LEFT JOIN FETCH vu.voucher
    LEFT JOIN FETCH b.slots s
    WHERE b.id IN :ids
""")
List<Booking> findBranchBookingsWithDetailsByIds(@Param("ids") List<UUID> ids);

    /**
     * Dùng cho trang booking history của user
     */
    @Query("""
    SELECT DISTINCT b
    FROM Booking b
    LEFT JOIN FETCH b.branch br
    LEFT JOIN FETCH br.admin
    LEFT JOIN FETCH b.pitch p
    LEFT JOIN FETCH p.branch pb
    LEFT JOIN FETCH pb.admin
    LEFT JOIN FETCH p.pitchType
    LEFT JOIN FETCH b.voucherUsage vu
    LEFT JOIN FETCH vu.voucher
    LEFT JOIN FETCH b.slots s
    WHERE b.user.id = :userId
    ORDER BY b.createdAt DESC
""")
List<Booking> findBookingsForHistoryByUser(@Param("userId") UUID userId);

    /**
     * Dùng khi bấm View detail
     */
    @Query("""
        SELECT DISTINCT b
        FROM Booking b
        LEFT JOIN FETCH b.pitch
        LEFT JOIN FETCH b.branch
        LEFT JOIN FETCH b.user
        LEFT JOIN FETCH b.slots
        LEFT JOIN FETCH b.voucherUsage
        WHERE b.id = :bookingId
    """)
    Optional<Booking> findBookingDetailById(@Param("bookingId") UUID bookingId);

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
    List<Object[]> calculateSystemMonthlyRevenue(@Param("year") int year);

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
    List<Object[]> calculateRevenueGroupedByBranch(@Param("year") int year);
}
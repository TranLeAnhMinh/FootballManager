package com.example.footballmanagement.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.BookingSlot;

@Repository
public interface BookingSlotRepository extends JpaRepository<BookingSlot, UUID> {
    
    // ✅ Check overlap (chỉ với booking còn hiệu lực: PENDING, APPROVED)
  @Query("""
    SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
    FROM BookingSlot s
    WHERE s.pitch.id = :pitchId
      AND s.endAt > :startAt
      AND s.startAt < :endAt
      AND s.booking.status IN ('PENDING','APPROVED')
  """)
boolean existsOverlap(UUID pitchId, OffsetDateTime startAt, OffsetDateTime endAt);

    // ✅ Lấy slot APPROVED theo pitch
    @Query("""
        SELECT s FROM BookingSlot s
        WHERE s.pitch.id = :pitchId
          AND s.booking.status = 'APPROVED'
    """)
    List<BookingSlot> findApprovedByPitch_Id(UUID pitchId);

    // ✅ Lấy slot APPROVED trong khoảng thời gian
    @Query("""
        SELECT s FROM BookingSlot s
        WHERE s.pitch.id = :pitchId
          AND s.endAt > :from
          AND s.startAt < :to
          AND s.booking.status = 'APPROVED'
    """)
    List<BookingSlot> findApprovedByPitch_IdAndRange(
            UUID pitchId,
            OffsetDateTime from,
            OffsetDateTime to
    );

    void deleteByBookingId(UUID bookingId);

    // ✅ Dùng cho API check-overlap khi tạo maintenance window
    @Query("""
        SELECT s FROM BookingSlot s
        WHERE s.pitch.id = :pitchId
          AND s.endAt > :startAt
          AND s.startAt < :endAt
          AND s.booking.status IN ('PENDING','APPROVED')
    """)
    List<BookingSlot> findOverlappedSlots(
            UUID pitchId,
            OffsetDateTime startAt,
            OffsetDateTime endAt
    );
}

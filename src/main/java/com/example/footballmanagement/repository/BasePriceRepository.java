package com.example.footballmanagement.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.BasePrice;

@Repository
public interface BasePriceRepository extends JpaRepository<BasePrice, UUID> {

    List<BasePrice> findByPitch_IdAndDayOfWeekAndTimeStartGreaterThanEqualAndTimeEndLessThan(
            UUID pitchId,
            short dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    );

    Optional<BasePrice> findByPitch_IdAndDayOfWeekAndTimeStartAndTimeEnd(
            UUID pitchId,
            short dayOfWeek,
            LocalTime timeStart,
            LocalTime timeEnd
    );

    List<BasePrice> findByPitch_IdOrderByDayOfWeekAscTimeStartAsc(UUID pitchId);

    long countByPitch_Id(UUID pitchId);

    /**
     * Lấy danh sách pitchId đã cấu hình đủ 224 block giá
     */
    @Query("""
        SELECT bp.pitch.id
        FROM BasePrice bp
        WHERE bp.pitch.id IN :pitchIds
        GROUP BY bp.pitch.id
        HAVING COUNT(bp.id) = 224
    """)
    List<UUID> findFullyConfiguredPitchIds(@Param("pitchIds") List<UUID> pitchIds);
}
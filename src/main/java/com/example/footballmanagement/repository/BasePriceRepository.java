package com.example.footballmanagement.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.BasePrice;

@Repository
public interface BasePriceRepository extends JpaRepository<BasePrice, UUID> {

    /**
     * Lấy tất cả block (45 phút) của pitch trong khoảng [startTime, endTime).
     * Ví dụ: user chọn 18:00–19:30 → sẽ trả về 2 block:
     *   - 18:00–18:45
     *   - 18:45–19:30
     */
    List<BasePrice> findByPitch_IdAndDayOfWeekAndTimeStartGreaterThanEqualAndTimeEndLessThan(
            UUID pitchId,
            short dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    );
}

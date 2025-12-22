package com.example.footballmanagement.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.MaintenanceWindow;

@Repository
public interface MaintenanceWindowRepository extends JpaRepository<MaintenanceWindow, UUID> {

    // ✅ Check overlap với khoảng thời gian (dùng khi booking)
    @Query("""
        SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END
        FROM MaintenanceWindow m
        WHERE m.pitch.id = :pitchId
          AND m.endAt > :startAt
          AND m.startAt < :endAt
    """)
    boolean existsOverlap(UUID pitchId, OffsetDateTime startAt, OffsetDateTime endAt);

    // ✅ Lấy toàn bộ window của 1 pitch (FE load calendar mặc định)
    List<MaintenanceWindow> findByPitch_Id(UUID pitchId);

    // ✅ Lấy window của pitch nhưng có filter khoảng thời gian (tối ưu khi FE chỉ load 1 tháng)
    List<MaintenanceWindow> findByPitch_IdAndEndAtAfterAndStartAtBefore(
            UUID pitchId,
            OffsetDateTime from,
            OffsetDateTime to
    );

    // ✅ Lấy lịch sử bảo trì của tất cả pitch trong 1 branch (phân trang + filter)
  @Query("""
    SELECT m
    FROM MaintenanceWindow m
    JOIN m.pitch p
    WHERE p.branch.id = :branchId
      AND (COALESCE(:pitchName, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:pitchName AS string), '%')))
      AND (COALESCE(:startFrom, NULL) IS NULL OR m.endAt >= :startFrom)
      AND (COALESCE(:endTo, NULL) IS NULL OR m.startAt <= :endTo)
    ORDER BY m.startAt DESC
""")
Page<MaintenanceWindow> findByBranchWithFilter(
        UUID branchId,
        String pitchName,
        OffsetDateTime startFrom,
        OffsetDateTime endTo,
        Pageable pageable
);

}

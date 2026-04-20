package com.example.footballmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.Pitch;

@Repository
public interface PitchRepository extends JpaRepository<Pitch, UUID> {

    // 🔹 Lấy toàn bộ pitch đang hoạt động (cho admin system)
    List<Pitch> findByActiveTrue();

    // 🔹 Lấy pitch theo branch mà admin branch quản lý
    // ⚠️ Method cũ, không nên dùng cho màn list admin branch nữa
    List<Pitch> findByBranch_Id(UUID branchId);

    // ✅ Method mới: fetch sẵn data cần cho list admin branch
    @EntityGraph(attributePaths = {
            "branch",
            "pitchType",
            "images",
    })
    List<Pitch> findAllWithAdminBranchDataByBranch_Id(UUID branchId);

    // 🔹 Lấy pitch theo loại sân và còn hoạt động
    List<Pitch> findByPitchType_IdAndActiveTrue(Short pitchTypeId);

    // 🔹 Lấy pitch theo tên chi nhánh / tên sân / trạng thái (lọc động)
    @Query("""
    SELECT p FROM Pitch p
    WHERE (COALESCE(:branchName, '') = '' OR LOWER(p.branch.name) LIKE LOWER(CONCAT('%', :branchName, '%')))
      AND (COALESCE(:pitchName, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :pitchName, '%')))
      AND (:active IS NULL OR p.active = :active)
    """)
    List<Pitch> findByFilters(String branchName, String pitchName, Boolean active);

    // === GET PITCH DETAIL (FULL DATA FOR ADMIN SYSTEM) ===
    @EntityGraph(attributePaths = {
            "branch",
            "pitchType",
            "images"
    })
    Pitch findWithDetailById(UUID id);
}
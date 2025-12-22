package com.example.footballmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.Branch;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {

    Optional<Branch> findByAdmin_Id(UUID adminId);

    /* ================= ADMIN SYSTEM ================= */

    // EntityGraph (Khoẻ + gọn)
    @EntityGraph(attributePaths = {
            "pitches",
            "pitches.pitchType"
    })
    @Query("SELECT b FROM Branch b ORDER BY b.name ASC")
    List<Branch> findAllWithPitches();
    boolean existsByIdAndAdminIsNotNull(UUID id);

List<Branch> findAllByAdminIsNull();

@Modifying
@Query("UPDATE Branch b SET b.admin.id = :userId WHERE b.id = :branchId")
int assignAdmin(UUID branchId, UUID userId);
/* Lấy danh sách Branch active & chưa có admin */
@Query("SELECT b FROM Branch b WHERE b.admin IS NULL AND b.active = true ORDER BY b.name ASC")
List<Branch> findAvailableForAssign();
}

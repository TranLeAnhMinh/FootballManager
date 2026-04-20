package com.example.footballmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByPitch_Id(UUID pitchId);

    Page<Review> findByPitch_IdOrderByCreatedAtDesc(UUID pitchId, Pageable pageable);

    boolean existsByPitch_IdAndUser_Id(UUID pitchId, UUID userId);

    Optional<Review> findByPitch_IdAndUser_Id(UUID pitchId, UUID userId);

    // Query batch lấy average rating cho nhiều pitch
    @Query("""
        SELECT r.pitch.id, AVG(r.rating)
        FROM Review r
        WHERE r.pitch.id IN :pitchIds
        GROUP BY r.pitch.id
    """)
    List<Object[]> findAverageRatingByPitchIds(@Param("pitchIds") List<UUID> pitchIds);
}
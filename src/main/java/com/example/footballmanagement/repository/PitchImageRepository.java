package com.example.footballmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.PitchImage;


@Repository
public interface PitchImageRepository extends JpaRepository<PitchImage, UUID> {

    // Tìm ảnh cover duy nhất của 1 pitch
    Optional<PitchImage> findByPitch_IdAndIsCoverTrue(UUID pitchId);

    // Tìm tất cả ảnh phụ (isCover = false)
    List<PitchImage> findByPitch_IdAndIsCoverFalse(UUID pitchId);
    
}

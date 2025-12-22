package com.example.footballmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.footballmanagement.entity.Review;
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    // ✅ Trả về toàn bộ review theo pitch (không phân trang)
    List<Review> findByPitch_Id(UUID pitchId);

    // Lấy tất cả review của 1 pitch (Phân trang)
   Page<Review> findByPitch_IdOrderByCreatedAtDesc(UUID pitchId, Pageable pageable);

    // Kiểm tra 1 user đã review sân này chưa
    boolean existsByPitch_IdAndUser_Id(UUID pitchId, UUID userId);

    //Lấy review của 1 user cụ thể để chinhe sửa hay xóa review
    Optional<Review> findByPitch_IdAndUser_Id(UUID pitchId, UUID userId);

}

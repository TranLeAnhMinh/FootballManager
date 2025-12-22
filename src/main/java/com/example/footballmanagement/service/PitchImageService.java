package com.example.footballmanagement.service;

import java.util.List;
import java.util.UUID;

import com.example.footballmanagement.dto.response.PitchImageResponse;

public interface PitchImageService {

     // Lấy ảnh cover (nếu có)
    PitchImageResponse getCoverImage(UUID pitchId);

    // Lấy danh sách ảnh phụ
    List<PitchImageResponse> getGalleryImages(UUID pitchId);
    
}

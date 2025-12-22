package com.example.footballmanagement.controller.api;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.dto.response.PitchImageResponse;
import com.example.footballmanagement.service.PitchImageService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/pitches/{pitchId}/images")
@RequiredArgsConstructor
public class PitchImageController {

    private final PitchImageService pitchImageService;

    // Lấy ảnh cover
    @GetMapping("/cover")
    public PitchImageResponse getCoverImage(@PathVariable UUID pitchId) {
        return pitchImageService.getCoverImage(pitchId);
    }

    // Lấy danh sách ảnh gallery
    @GetMapping("/gallery")
    public List<PitchImageResponse> getGalleryImages(@PathVariable UUID pitchId) {
        return pitchImageService.getGalleryImages(pitchId);
    }
    
}

package com.example.footballmanagement.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.footballmanagement.dto.response.PitchImageResponse;
import com.example.footballmanagement.entity.PitchImage;
import com.example.footballmanagement.repository.PitchImageRepository;
import com.example.footballmanagement.service.PitchImageService;
import com.example.footballmanagement.utils.ConverterUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PitchImageServiceImpl implements PitchImageService {

    private final PitchImageRepository pitchImageRepository;

    @Override
    public PitchImageResponse getCoverImage(UUID pitchId) {
        PitchImage cover = pitchImageRepository.findByPitch_IdAndIsCoverTrue(pitchId)
                .orElseThrow(() -> new RuntimeException("Cover image not found for pitch " + pitchId));

        return ConverterUtil.toPitchImageResponse(cover);
    }

    @Override
    public List<PitchImageResponse> getGalleryImages(UUID pitchId) {
        return pitchImageRepository.findByPitch_IdAndIsCoverFalse(pitchId).stream()
                .map(ConverterUtil::toPitchImageResponse)
                .collect(Collectors.toList());
    }
}

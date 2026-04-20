package com.example.footballmanagement.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.footballmanagement.dto.common.ImageUploadResult;
import com.example.footballmanagement.service.ImageStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryImageStorageService implements ImageStorageService {

    private static final long MAX_FILE_SIZE = 15  * 1024 * 1024; // 10MB

    private final Cloudinary cloudinary;

    @Override
    public ImageUploadResult upload(MultipartFile file, String folder) {
        validateFile(file);

        try {
            String originalFileName = file.getOriginalFilename();
            String publicId = buildPublicId(folder);

            @SuppressWarnings("rawtypes")
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", true
                    )
            );

            String secureUrl = Objects.toString(uploadResult.get("secure_url"), null);
            String uploadedPublicId = Objects.toString(uploadResult.get("public_id"), null);

            log.info("Cloudinary upload success: publicId={}, url={}", uploadedPublicId, secureUrl);

            return new ImageUploadResult(
                    secureUrl,
                    uploadedPublicId,
                    originalFileName
            );

        } catch (IOException e) {
            log.error("Cloudinary upload failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }

    @Override
    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            log.warn("Skip Cloudinary delete because publicId is null or blank");
            return;
        }

        try {
            @SuppressWarnings("rawtypes")
            Map deleteResult = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
            );

            Object resultValue = deleteResult.get("result");
            String result = resultValue == null ? null : resultValue.toString();

            log.info("Cloudinary delete result: publicId={}, result={}", publicId, deleteResult);

            if (!"ok".equalsIgnoreCase(result) && !"not found".equalsIgnoreCase(result)) {
                throw new RuntimeException("Cloudinary delete failed for publicId=" + publicId + ", result=" + deleteResult);
            }

        } catch (IOException e) {
            log.error("Cloudinary delete failed: publicId={}, error={}", publicId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete image from Cloudinary", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 15MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }

    private String buildPublicId(String folder) {
        String safeFolder = (folder == null || folder.isBlank()) ? "uploads" : folder.trim();
        return safeFolder + "/" + UUID.randomUUID();
    }
}
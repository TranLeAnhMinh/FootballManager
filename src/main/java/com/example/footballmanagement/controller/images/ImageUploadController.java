package com.example.footballmanagement.controller.images;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.footballmanagement.dto.common.ImageUploadResult;
import com.example.footballmanagement.service.ImageStorageService;
import com.example.footballmanagement.service.PitchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/adminsystem/images")
@RequiredArgsConstructor
@Slf4j
public class ImageUploadController {

    private final ImageStorageService imageStorageService;
    private final PitchService pitchService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPitchImage(@RequestParam("file") MultipartFile file) {
        try {
            ImageUploadResult result = imageStorageService.upload(file, "pitchimages");

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Upload successful");
            response.put("url", result.getUrl());
            response.put("publicId", result.getPublicId());
            response.put("originalFileName", result.getOriginalFileName());

            log.info("Uploaded image to Cloudinary: url={}, publicId={}", result.getUrl(), result.getPublicId());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid upload request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RuntimeException e) {
            log.error("Upload failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable UUID imageId) {

        pitchService.deletePitchImage(imageId);

        return ResponseEntity.ok("Image deleted successfully");
    }
}
package com.example.footballmanagement.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.footballmanagement.dto.common.ImageUploadResult;

public interface ImageStorageService {

    ImageUploadResult upload(MultipartFile file, String folder);

    void delete(String publicId);
}
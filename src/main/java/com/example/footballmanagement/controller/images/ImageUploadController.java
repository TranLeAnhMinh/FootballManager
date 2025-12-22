package com.example.footballmanagement.controller.images;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/adminsystem/images")
@Slf4j
public class ImageUploadController {

    // 🔹 Đường dẫn thư mục chứa ảnh trong project
    private static final String UPLOAD_DIR = "src/main/resources/static/images/pitchimages/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPitchImage(@RequestParam("file") MultipartFile file) {
        try {
            // 🔹 Kiểm tra file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("❌ File upload is empty");
            }

            // 🔹 Lấy tên gốc của file
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isBlank()) {
                return ResponseEntity.badRequest().body("❌ Invalid file name");
            }

            // 🔹 Tạo tên file unique
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uuidShort = UUID.randomUUID().toString().substring(0, 8);
            String uniqueName = timestamp + "_" + uuidShort + "_" + originalFileName;

            // 🔹 Tạo folder nếu chưa tồn tại
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 🔹 Ghi file
            Path filePath = uploadPath.resolve(uniqueName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 🔹 Tạo URL public để lưu vào DB
            String fileUrl = "/images/pitchimages/" + uniqueName;
            log.info("✅ Uploaded: {}", fileUrl);

            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            log.error("❌ Upload error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}

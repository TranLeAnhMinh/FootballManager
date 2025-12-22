package com.example.footballmanagement.controller.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.example.footballmanagement.constant.Endpoint.PITCH_ADMIN_SYSTEM_API_BASE;
import com.example.footballmanagement.dto.request.PitchCreateRequest;
import com.example.footballmanagement.dto.request.PitchUpdateRequest;
import com.example.footballmanagement.dto.request.PitchesFilterRequestDto;
import com.example.footballmanagement.dto.response.PitchCreateResponse;
import com.example.footballmanagement.dto.response.PitchDetaiAdminsystemlResponse;
import com.example.footballmanagement.dto.response.PitchResponseDto;
import com.example.footballmanagement.dto.response.PitchUpdateResponse;
import com.example.footballmanagement.service.PitchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(PITCH_ADMIN_SYSTEM_API_BASE)
@RequiredArgsConstructor
public class PitchAdminSystemController {

    private final PitchService pitchService;

    // 🔹 GET /api/adminsystem/pitches
    @GetMapping
    public ResponseEntity<List<PitchResponseDto>> getAllPitchesForAdminSystem(
            @ModelAttribute PitchesFilterRequestDto request) {

        List<PitchResponseDto> response = pitchService.getAllPitchesForAdmin(request);
        return ResponseEntity.ok(response);
    }

    // 🔹 GET /api/adminsystem/pitches/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PitchDetaiAdminsystemlResponse> getPitchDetail(
            @PathVariable UUID id) {

        var response = pitchService.getPitchDetailAdmin(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 🔹 API: Tạo sân mới và upload ảnh
     * URL: POST /api/adminsystem/pitches/upload-and-create
     */
    @PostMapping(
            value = "/upload-and-create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<PitchCreateResponse> uploadAndCreatePitch(
            @RequestPart("pitch") String pitchJson,
            @RequestPart(value = "file", required = false) List<MultipartFile> files) {

        PitchCreateRequest request = pitchService.parsePitchRequest(pitchJson);

        PitchCreateResponse response = pitchService.createPitchWithFiles(request, files);
        return ResponseEntity.ok(response);
    }
    // ===============================================
// 🔹 UPDATE PITCH (Admin System)
// URL: PUT /api/adminsystem/pitches/{id}
// ===============================================
@PutMapping("/{id}")
public ResponseEntity<PitchUpdateResponse> updatePitch(
        @PathVariable UUID id,
        @RequestBody PitchUpdateRequest request
) {
    PitchUpdateResponse response = pitchService.updatePitch(id, request);
    return ResponseEntity.ok(response);
}
}

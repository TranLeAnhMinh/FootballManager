package com.example.footballmanagement.controller.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.dto.request.PitchTypeRequest;
import com.example.footballmanagement.dto.response.PitchDetailResponse;
import com.example.footballmanagement.dto.response.PitchTypeBranchesResponse;
import com.example.footballmanagement.service.PitchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pitches")
@RequiredArgsConstructor
public class PitchController {

    private final PitchService pitchService;

    // API: Trả về danh sách branch + pitch theo pitch type
    @PostMapping("/by-type")
    public ResponseEntity<PitchTypeBranchesResponse> getBranchesAndPitchesByType(
            @RequestBody PitchTypeRequest request
    ) {
        PitchTypeBranchesResponse response = pitchService.getBranchesAndPitchesByType(request);
        return ResponseEntity.ok(response);
    }

    // Lấy chi tiết 1 pitch
    @GetMapping("/{id}")
    public ResponseEntity<PitchDetailResponse> getPitchDetail(@PathVariable UUID id) {
        PitchDetailResponse response = pitchService.getPitchDetail(id);
        return ResponseEntity.ok(response);
    }
}

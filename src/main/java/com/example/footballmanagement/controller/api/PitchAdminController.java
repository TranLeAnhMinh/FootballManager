package com.example.footballmanagement.controller.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.constant.Endpoint;
import com.example.footballmanagement.dto.response.PitchTypeDetailResponse;
import com.example.footballmanagement.service.PitchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Endpoint.PITCH_ADMIN_API_BASE)
@RequiredArgsConstructor
public class PitchAdminController {

    private final PitchService pitchService;

    /**
     * ✅ API: Lấy toàn bộ sân trong chi nhánh mà admin_branch đang quản lý
     */
    @GetMapping
    public ResponseEntity<List<PitchTypeDetailResponse>> getPitchesByAdminBranch(
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID adminId = userDetails.getId();
        List<PitchTypeDetailResponse> response = pitchService.getPitchesByAdminBranch(adminId);
        return ResponseEntity.ok(response);
    }
}

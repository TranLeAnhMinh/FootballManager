package com.example.footballmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.example.footballmanagement.dto.request.PitchCreateRequest;
import com.example.footballmanagement.dto.request.PitchTypeRequest;
import com.example.footballmanagement.dto.request.PitchUpdateRequest;
import com.example.footballmanagement.dto.request.PitchesFilterRequestDto;
import com.example.footballmanagement.dto.response.PitchCreateResponse;
import com.example.footballmanagement.dto.response.PitchDetaiAdminsystemlResponse;
import com.example.footballmanagement.dto.response.PitchDetailResponse;
import com.example.footballmanagement.dto.response.PitchResponseDto;
import com.example.footballmanagement.dto.response.PitchTypeBranchesResponse;
import com.example.footballmanagement.dto.response.PitchTypeDetailResponse;
import com.example.footballmanagement.dto.response.PitchUpdateResponse;
import com.example.footballmanagement.entity.Pitch;
public interface PitchService {
    PitchTypeBranchesResponse getBranchesAndPitchesByType(PitchTypeRequest request);

    PitchDetailResponse getPitchDetail(UUID pitchId);

    Pitch getPitchEntity(UUID pitchId);

    // Lấy toàn bộ pitch (group theo loại sân) trong branch mà admin_branch đang quản lý
    List<PitchTypeDetailResponse> getPitchesByAdminBranch(UUID adminId);

    // ⚡ Dành cho Admin System xem toàn bộ pitches trên hệ thống
    List<PitchResponseDto> getAllPitchesForAdmin(PitchesFilterRequestDto request);

    PitchCreateResponse createPitchWithFiles(PitchCreateRequest request, List<MultipartFile> files);
    PitchCreateRequest parsePitchRequest(String pitchJson);

    PitchDetaiAdminsystemlResponse getPitchDetailAdmin(UUID pitchId);

    PitchUpdateResponse updatePitch(UUID pitchId, PitchUpdateRequest request);

}
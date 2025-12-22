package com.example.footballmanagement.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.example.footballmanagement.dto.request.BranchCreateRequest;
import com.example.footballmanagement.dto.response.BranchResponse;
import com.example.footballmanagement.dto.response.BranchResponse.PitchSummary;
import com.example.footballmanagement.dto.response.BranchResponseDto;
import com.example.footballmanagement.entity.Branch;
import com.example.footballmanagement.entity.Pitch;
import com.example.footballmanagement.repository.BranchRepository;
import com.example.footballmanagement.service.BranchService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public BranchResponseDto createBranch(BranchCreateRequest request) {
        // 🔹 Validate cơ bản
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Branch name cannot be empty");
        }

        // 🔹 Tạo entity mới
        Branch branch = Branch.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .active(true)
                .build();

        // 🔹 Lưu DB
        Branch saved = branchRepository.save(branch);

        // 🔹 Map sang DTO
        return BranchResponseDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .location(saved.getLocation())
                .description(saved.getDescription())
                .build();
    }

      @Override
    public List<BranchResponse> getAllBranchesWithPitches() {
        return branchRepository.findAllWithPitches()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* =========== PRIVATE MAPPERS =========== */

    private BranchResponse mapToResponse(Branch b) {
        return BranchResponse.builder()
                .id(b.getId())
                .name(b.getName())
                .location(b.getLocation())
                .description(b.getDescription())
                .pitches(mapPitchList(b.getPitches()))
                .build();
    }

    private List<PitchSummary> mapPitchList(List<Pitch> pitches) {
        if (pitches == null) return List.of();

        return pitches.stream().map(p ->
                PitchSummary.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .location(p.getLocation())
                        .description(p.getDescription())
                        .active(p.isActive())
                        .pitchTypeId(p.getPitchType().getId())
                        .pitchTypeName(p.getPitchType().getName())
                        .build()
        ).toList();
    }
}

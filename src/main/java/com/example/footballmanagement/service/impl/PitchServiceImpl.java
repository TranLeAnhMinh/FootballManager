package com.example.footballmanagement.service.impl;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.footballmanagement.dto.request.PitchCreateRequest;
import com.example.footballmanagement.dto.request.PitchTypeRequest;
import com.example.footballmanagement.dto.request.PitchUpdateRequest;
import com.example.footballmanagement.dto.request.PitchesFilterRequestDto;
import com.example.footballmanagement.dto.response.PitchCreateResponse;
import com.example.footballmanagement.dto.response.PitchDetaiAdminsystemlResponse;
import com.example.footballmanagement.dto.response.PitchDetailResponse;
import com.example.footballmanagement.dto.response.PitchImageResponse;
import com.example.footballmanagement.dto.response.PitchResponseDto;
import com.example.footballmanagement.dto.response.PitchSummaryResponse;
import com.example.footballmanagement.dto.response.PitchTypeBranchesResponse;
import com.example.footballmanagement.dto.response.PitchTypeDetailResponse;
import com.example.footballmanagement.dto.response.PitchUpdateResponse;
import com.example.footballmanagement.entity.Branch;
import com.example.footballmanagement.entity.Pitch;
import com.example.footballmanagement.entity.PitchImage;
import com.example.footballmanagement.entity.PitchType;
import com.example.footballmanagement.repository.BranchRepository;
import com.example.footballmanagement.repository.PitchRepository;
import com.example.footballmanagement.repository.PitchTypeRepository;
import com.example.footballmanagement.service.PitchService;
import com.example.footballmanagement.utils.ConverterUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PitchServiceImpl implements PitchService {

    private final PitchRepository pitchRepository;
    private final PitchTypeRepository pitchTypeRepository;
    private final BranchRepository branchRepository;

    @Override
    public PitchTypeBranchesResponse getBranchesAndPitchesByType(PitchTypeRequest request) {
        Short pitchTypeId = request.getPitchTypeId();

        // Lấy PitchType để lấy tên hiển thị
        PitchType pitchType = pitchTypeRepository.findById(pitchTypeId)
                .orElseThrow(() -> new RuntimeException("PitchType not found: " + pitchTypeId));

        // Lấy tất cả pitch còn hoạt động theo loại sân
        List<Pitch> pitches = pitchRepository.findByPitchType_IdAndActiveTrue(pitchTypeId);

        // Group pitch theo branch
        Map<UUID, List<Pitch>> branchPitchMap = pitches.stream()
                .collect(Collectors.groupingBy(p -> p.getBranch().getId()));

        // Convert sang BranchSummaryDTO
        List<PitchTypeBranchesResponse.BranchSummaryDTO> branchDTOs = branchPitchMap.entrySet().stream()
                .map(entry -> {
                    List<Pitch> branchPitches = entry.getValue();
                    // chỗ sửa: dùng ConverterUtil
                    return ConverterUtil.toBranchSummaryDTO(branchPitches.get(0).getBranch(), branchPitches);
                })
                .collect(Collectors.toList());

        // Trả về response
        return PitchTypeBranchesResponse.builder()
                .pitchTypeId(pitchType.getId())
                .pitchTypeName(pitchType.getName())
                .branches(branchDTOs)
                .build();
    }

    @Override
    public PitchDetailResponse getPitchDetail(UUID pitchId) {
    Pitch pitch = pitchRepository.findById(pitchId)
            .orElseThrow(() -> new RuntimeException("Pitch not found: " + pitchId));

    return ConverterUtil.toPitchDetailResponse(pitch); 
    }

    @Override
    public Pitch getPitchEntity(UUID pitchId) {
        return pitchRepository.findById(pitchId)
            .orElseThrow(() -> new RuntimeException("Pitch not found: " + pitchId));
    }

     @Override
    public List<PitchTypeDetailResponse> getPitchesByAdminBranch(UUID adminId) {
        // 1️⃣ Lấy branch mà admin quản lý
        var branch = branchRepository.findByAdmin_Id(adminId)
            .orElseThrow(() -> new RuntimeException("Branch not found for this admin"));

        // 2️⃣ Lấy toàn bộ pitch trong branch đó
        List<Pitch> pitches = pitchRepository.findByBranch_Id(branch.getId());

        if (pitches.isEmpty()) {
            return List.of();
        }

        // 3️⃣ Group các pitch theo loại sân (PitchType)
        Map<Short, List<Pitch>> grouped = pitches.stream()
                .collect(Collectors.groupingBy(p -> p.getPitchType().getId()));

        // 4️⃣ Map sang DTO
        return grouped.entrySet().stream()
                .map(entry -> {
                    Short typeId = entry.getKey();
                    List<Pitch> typePitches = entry.getValue();

                    // Lấy tên loại sân từ pitch đầu tiên
                    String pitchTypeName = typePitches.get(0).getPitchType().getName();

                    // Map các pitch sang PitchSummaryResponse
                    List<PitchSummaryResponse> pitchSummaries = typePitches.stream()
                            .map(p -> {
                                String coverImageUrl = p.getImages().stream()
                                        .filter(img -> img.isCover())
                                        .map(img -> img.getUrl())
                                        .findFirst()
                                        .orElse(null);

                                double avgRating = (p.getReviews() == null || p.getReviews().isEmpty())
                                        ? 0
                                        : p.getReviews().stream()
                                                .mapToInt(r -> r.getRating())
                                                .average()
                                                .orElse(0);

                                return PitchSummaryResponse.builder()
                                        .id(p.getId())
                                        .name(p.getName())
                                        .location(p.getLocation())
                                        .description(p.getDescription())
                                        .active(p.isActive())
                                        .coverImageUrl(coverImageUrl)
                                        .averageRating(avgRating)
                                        .build();
                            })
                            .collect(Collectors.toList());

                    // Tạo PitchTypeDetailResponse
                    return PitchTypeDetailResponse.builder()
                            .id(typeId)
                            .name(pitchTypeName)
                            .pitches(pitchSummaries)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PitchResponseDto> getAllPitchesForAdmin(PitchesFilterRequestDto request) {
        // 🔹 Query động theo bộ lọc (branchName, pitchName, active)
        List<Pitch> pitches = pitchRepository.findByFilters(
                request.getBranchName(),
                request.getPitchName(),
                request.getActive()
        );

        // 🔹 Convert sang DTO
        return pitches.stream()
                .map(ConverterUtil::toPitchResponseDto)
                .collect(Collectors.toList());
    }

@Override
public PitchCreateResponse createPitchWithFiles(PitchCreateRequest request, List<MultipartFile> files) {

    // 🔹 Validate input
    if (request.getBranchId() == null)
        throw new IllegalArgumentException("Branch ID is required");
    if (request.getPitchTypeId() == null)
        throw new IllegalArgumentException("PitchType ID is required");

    // 🔹 Lấy Branch
    Branch branch = branchRepository.findById(request.getBranchId())
            .orElseThrow(() -> new RuntimeException("Branch not found: " + request.getBranchId()));

    // 🔹 Lấy PitchType
    PitchType pitchType = pitchTypeRepository.findById(request.getPitchTypeId())
            .orElseThrow(() -> new RuntimeException("PitchType not found: " + request.getPitchTypeId()));

    // 🔹 Tạo Pitch entity
    Pitch pitch = Pitch.builder()
            .name(request.getName())
            .location(request.getLocation())
            .description(request.getDescription())
            .active(true)
            .branch(branch)
            .pitchType(pitchType)
            .build();

    // 🔹 Nếu có file upload → xử lý lưu
    if (files != null && !files.isEmpty()) {
        String uploadDir = new File("src/main/resources/static/images/pitchimages").getAbsolutePath();

        List<PitchImage> imageEntities = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            try {
                String originalName = Objects.requireNonNull(file.getOriginalFilename());
                String uniqueName = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                        + "_" + UUID.randomUUID().toString().substring(0, 8)
                        + "_" + originalName;

                Path targetPath = Paths.get(uploadDir, uniqueName);
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // ✅ File đầu tiên là cover, còn lại là ảnh phụ
                boolean isCover = (i == 0);

                imageEntities.add(
                    PitchImage.builder()
                            .pitch(pitch)
                            .url("/images/pitchimages/" + uniqueName)
                            .isCover(isCover)
                            .build()
                );
            } catch (IOException e) {
                throw new RuntimeException("❌ Failed to save file: " + file.getOriginalFilename(), e);
            }
        }

        pitch.setImages(imageEntities);
    }

    // 🔹 Lưu Pitch + ảnh vào DB
    Pitch saved = pitchRepository.save(pitch);

    // 🔹 Trả về DTO
    List<PitchImageResponse> imageResponses = saved.getImages() == null ? List.of() :
            saved.getImages().stream()
                    .map(img -> PitchImageResponse.builder()
                            .id(img.getId())
                            .url(img.getUrl())
                            .isCover(img.isCover())
                            .build())
                    .toList();

    return PitchCreateResponse.builder()
            .id(saved.getId())
            .name(saved.getName())
            .location(saved.getLocation())
            .description(saved.getDescription())
            .active(saved.isActive())
            .branchId(branch.getId())
            .branchName(branch.getName())
            .branchLocation(branch.getLocation())
            .pitchTypeId(pitchType.getId())
            .pitchTypeName(pitchType.getName())
            .images(imageResponses)
            .build();
}

        private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
public PitchCreateRequest parsePitchRequest(String pitchJson) {
    try {
        return objectMapper.readValue(pitchJson, PitchCreateRequest.class);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
        throw new RuntimeException("❌ JSON không hợp lệ: " + e.getMessage(), e);
    }
}

@Override
public PitchDetaiAdminsystemlResponse getPitchDetailAdmin(UUID pitchId) {

    var pitch = pitchRepository.findWithDetailById(pitchId);
    if (pitch == null) {
        throw new RuntimeException("Pitch not found: " + pitchId);
    }

    return PitchDetaiAdminsystemlResponse.builder()
            .id(pitch.getId())
            .branchId(pitch.getBranch().getId())
            .branchName(pitch.getBranch().getName())
            .branchLocation(pitch.getBranch().getLocation())
            .pitchTypeId(pitch.getPitchType().getId())
            .pitchTypeName(pitch.getPitchType().getName())
            .name(pitch.getName())
            .location(pitch.getLocation())
            .description(pitch.getDescription())
            .active(pitch.isActive())
            .images(
                    pitch.getImages() == null ? List.of() :
                     pitch.getImages().stream()
                        .map(img -> PitchDetaiAdminsystemlResponse.ImageDto.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .cover(img.isCover())
                                .build()
                        )
                        .toList()
            )
            .build();
}

@Override
public PitchUpdateResponse updatePitch(UUID pitchId, PitchUpdateRequest request) {

    // 1️⃣ Lấy pitch từ DB
    Pitch pitch = pitchRepository.findById(pitchId)
            .orElseThrow(() -> new RuntimeException("Pitch not found: " + pitchId));

    // 2️⃣ Lấy loại sân mới
    PitchType pitchType = pitchTypeRepository.findById(request.getPitchTypeId())
            .orElseThrow(() -> new RuntimeException("PitchType not found: " + request.getPitchTypeId()));

    // 3️⃣ Cập nhật thông tin
    pitch.setName(request.getName());
    pitch.setLocation(request.getLocation());
    pitch.setDescription(request.getDescription());
    pitch.setActive(request.getActive());
    pitch.setPitchType(pitchType);

    // 4️⃣ Lưu DB
    Pitch updated = pitchRepository.save(pitch);

    // 5️⃣ Convert sang Response
    return PitchUpdateResponse.builder()
            .id(updated.getId())
            .name(updated.getName())
            .location(updated.getLocation())
            .description(updated.getDescription())
            .active(updated.isActive())
            .pitchTypeId(updated.getPitchType().getId())
            .pitchTypeName(updated.getPitchType().getName())
            .branchId(updated.getBranch().getId())
            .branchName(updated.getBranch().getName())
            .build();
}


}

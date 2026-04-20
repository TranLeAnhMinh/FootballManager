package com.example.footballmanagement.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.footballmanagement.dto.common.ImageUploadResult;
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
import com.example.footballmanagement.repository.PitchImageRepository;
import com.example.footballmanagement.repository.PitchRepository;
import com.example.footballmanagement.repository.PitchTypeRepository;
import com.example.footballmanagement.service.BasePriceService;
import com.example.footballmanagement.service.ImageStorageService;
import com.example.footballmanagement.service.PitchService;
import com.example.footballmanagement.service.ReviewService;
import com.example.footballmanagement.utils.ConverterUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PitchServiceImpl implements PitchService {
    private final ReviewService reviewService;
    private final BasePriceService basePriceService;
    private final PitchRepository pitchRepository;
    private final PitchTypeRepository pitchTypeRepository;
    private final BranchRepository branchRepository;
    private final ImageStorageService imageStorageService;
    private final PitchImageRepository pitchImageRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PitchTypeBranchesResponse getBranchesAndPitchesByType(PitchTypeRequest request) {
        Short pitchTypeId = request.getPitchTypeId();

        PitchType pitchType = pitchTypeRepository.findById(pitchTypeId)
                .orElseThrow(() -> new RuntimeException("PitchType not found: " + pitchTypeId));

        List<Pitch> pitches = pitchRepository.findByPitchType_IdAndActiveTrue(pitchTypeId);

        var branchPitchMap = pitches.stream()
                .collect(Collectors.groupingBy(p -> p.getBranch().getId()));

        List<PitchTypeBranchesResponse.BranchSummaryDTO> branchDTOs = branchPitchMap.entrySet().stream()
                .map(entry -> {
                    List<Pitch> branchPitches = entry.getValue();
                    return ConverterUtil.toBranchSummaryDTO(branchPitches.get(0).getBranch(), branchPitches);
                })
                .collect(Collectors.toList());

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
    var branch = branchRepository.findByAdmin_Id(adminId)
            .orElseThrow(() -> new RuntimeException("Branch not found for this admin"));

    List<Pitch> pitches = pitchRepository.findAllWithAdminBranchDataByBranch_Id(branch.getId());

    if (pitches.isEmpty()) {
        return List.of();
    }

    List<UUID> pitchIds = pitches.stream()
            .map(Pitch::getId)
            .toList();

    Set<UUID> fullyConfiguredPitchIds = basePriceService.getFullyConfiguredPitchIds(pitchIds);
    Map<UUID, Double> averageRatingMap = reviewService.getAverageRatingMap(pitchIds);

    var grouped = pitches.stream()
            .collect(Collectors.groupingBy(p -> p.getPitchType().getId()));

    return grouped.entrySet().stream()
            .map(entry -> {
                Short typeId = entry.getKey();
                List<Pitch> typePitches = entry.getValue();

                String pitchTypeName = typePitches.get(0).getPitchType().getName();

                List<PitchSummaryResponse> pitchSummaries = typePitches.stream()
                        .map(p -> {
                            String coverImageUrl = p.getImages().stream()
                                    .filter(PitchImage::isCover)
                                    .map(PitchImage::getUrl)
                                    .findFirst()
                                    .orElse(null);

                            double avgRating = averageRatingMap.getOrDefault(p.getId(), 0.0);

                            return PitchSummaryResponse.builder()
                                    .id(p.getId())
                                    .name(p.getName())
                                    .location(p.getLocation())
                                    .description(p.getDescription())
                                    .active(p.isActive())
                                    .coverImageUrl(coverImageUrl)
                                    .averageRating(avgRating)
                                    .priceConfigComplete(fullyConfiguredPitchIds.contains(p.getId()))
                                    .build();
                        })
                        .collect(Collectors.toList());

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
        List<Pitch> pitches = pitchRepository.findByFilters(
                request.getBranchName(),
                request.getPitchName(),
                request.getActive()
        );

        return pitches.stream()
                .map(ConverterUtil::toPitchResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PitchCreateResponse createPitchWithFiles(
            PitchCreateRequest request,
            List<MultipartFile> files
    ) {
        if (request.getBranchId() == null) {
            throw new IllegalArgumentException("Branch ID is required");
        }
        if (request.getPitchTypeId() == null) {
            throw new IllegalArgumentException("PitchType ID is required");
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found: " + request.getBranchId()));

        PitchType pitchType = pitchTypeRepository.findById(request.getPitchTypeId())
                .orElseThrow(() -> new RuntimeException("PitchType not found: " + request.getPitchTypeId()));

        Pitch pitch = Pitch.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .active(true)
                .branch(branch)
                .pitchType(pitchType)
                .build();

        if (files != null && !files.isEmpty()) {
            List<PitchImage> imageEntities = new ArrayList<>();

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }

                ImageUploadResult uploadResult = imageStorageService.upload(file, "pitchimages");
                boolean isCover = imageEntities.isEmpty();

                PitchImage.PitchImageBuilder imageBuilder = PitchImage.builder()
                        .pitch(pitch)
                        .url(uploadResult.getUrl())
                        .publicId(uploadResult.getPublicId())
                        .isCover(isCover);

                imageEntities.add(imageBuilder.build());
            }

            pitch.setImages(imageEntities);
        }

        Pitch saved = pitchRepository.save(pitch);

        List<PitchImageResponse> imageResponses = saved.getImages() == null
                ? List.of()
                : saved.getImages().stream()
                        .map(img -> PitchImageResponse.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .publicId(img.getPublicId())
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
                        pitch.getImages() == null ? List.of()
                                : pitch.getImages().stream()
                                        .map(img -> PitchDetaiAdminsystemlResponse.ImageDto.builder()
                                                .id(img.getId())
                                                .url(img.getUrl())
                                                .cover(img.isCover())
                                                .build())
                                        .toList()
                )
                .build();
    }

    @Override
    public PitchUpdateResponse updatePitch(UUID pitchId, PitchUpdateRequest request) {
        Pitch pitch = pitchRepository.findById(pitchId)
                .orElseThrow(() -> new RuntimeException("Pitch not found: " + pitchId));

        PitchType pitchType = pitchTypeRepository.findById(request.getPitchTypeId())
                .orElseThrow(() -> new RuntimeException("PitchType not found: " + request.getPitchTypeId()));

        pitch.setName(request.getName());
        pitch.setLocation(request.getLocation());
        pitch.setDescription(request.getDescription());
        pitch.setActive(request.getActive());
        pitch.setPitchType(pitchType);

        Pitch updated = pitchRepository.save(pitch);

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

    @Override
    public void deletePitchImage(UUID imageId) {
        PitchImage image = pitchImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));

        UUID pitchId = image.getPitch().getId();
        boolean isCover = image.isCover();
        String publicId = image.getPublicId();

        if (publicId != null && !publicId.isBlank()) {
            imageStorageService.delete(publicId);
        }

        pitchImageRepository.delete(image);

        if (isCover) {
            List<PitchImage> remainingImages = pitchImageRepository.findByPitch_Id(pitchId);

            if (!remainingImages.isEmpty()) {
                PitchImage newCover = remainingImages.get(0);
                newCover.setCover(true);
                pitchImageRepository.save(newCover);
            }
        }
    }

    @Override
    public List<PitchImageResponse> addPitchImages(UUID pitchId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("At least one image file is required");
        }

        Pitch pitch = pitchRepository.findById(pitchId)
                .orElseThrow(() -> new RuntimeException("Pitch not found: " + pitchId));

        List<PitchImage> existingImages = pitchImageRepository.findByPitch_Id(pitchId);
        boolean hasExistingImages = !existingImages.isEmpty();

        List<PitchImage> savedImages = new ArrayList<>();
        boolean coverAssignedInThisBatch = false;

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            ImageUploadResult uploadResult = imageStorageService.upload(file, "pitchimages");

            boolean isCover = false;
            if (!hasExistingImages && !coverAssignedInThisBatch) {
                isCover = true;
                coverAssignedInThisBatch = true;
            }

            PitchImage pitchImage = PitchImage.builder()
                    .pitch(pitch)
                    .url(uploadResult.getUrl())
                    .publicId(uploadResult.getPublicId())
                    .isCover(isCover)
                    .build();

            savedImages.add(pitchImageRepository.save(pitchImage));
        }

        if (savedImages.isEmpty()) {
            throw new IllegalArgumentException("No valid image files were uploaded");
        }

        return savedImages.stream()
                .map(img -> PitchImageResponse.builder()
                        .id(img.getId())
                        .url(img.getUrl())
                        .publicId(img.getPublicId())
                        .isCover(img.isCover())
                        .build())
                .toList();
    }
}
package com.example.footballmanagement.controller.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.constant.Endpoint;
import com.example.footballmanagement.dto.request.ApplyBasePriceTemplateRequest;
import com.example.footballmanagement.dto.request.UpdateBasePriceCellRequest;
import com.example.footballmanagement.dto.response.ApplyBasePriceTemplateResponse;
import com.example.footballmanagement.dto.response.BasePriceWeeklyGridResponse;
import com.example.footballmanagement.dto.response.UpdateBasePriceCellResponse;
import com.example.footballmanagement.service.BasePriceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(Endpoint.BASE_PRICE_ADMIN_SYSTEM_API_BASE)
@RequiredArgsConstructor
public class BasePriceAdminSystemController {

    private final BasePriceService basePriceService;

    /**
     * ✅ API dành cho ADMIN_SYSTEM:
     * Set giá sân theo template (range giờ)
     *
     * Ví dụ:
     * - Thứ 2 → Thứ 6: 00:00 - 17:15 = 200k
     * - Backend sẽ tự split thành block 45 phút
     */
    @PostMapping("/apply-template")
    public ResponseEntity<ApplyBasePriceTemplateResponse> applyBasePriceTemplate(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @Validated @RequestBody ApplyBasePriceTemplateRequest request
    ) {
        UUID adminSystemId = userDetails.getId();

        log.info(
                "💰 [ADMIN SYSTEM API] AdminSystem {} set base price template: pitchIds={}, days={}, {} -> {}, price={}",
                adminSystemId,
                request.getPitchIds(),
                request.getDayOfWeeks(),
                request.getStartTime(),
                request.getEndTime(),
                request.getPrice()
        );

        ApplyBasePriceTemplateResponse response =
                basePriceService.applyBasePriceTemplate(request);

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ API dành cho ADMIN_SYSTEM:
     * Lấy weekly grid giá của 1 sân
     */
    @GetMapping("/pitches/{pitchId}/weekly-grid")
    public ResponseEntity<BasePriceWeeklyGridResponse> getWeeklyGrid(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @PathVariable UUID pitchId
    ) {
        UUID adminSystemId = userDetails.getId();

        log.info(
                "📊 [ADMIN SYSTEM API] AdminSystem {} xem weekly grid base price của pitch {}",
                adminSystemId,
                pitchId
        );

        BasePriceWeeklyGridResponse response = basePriceService.getWeeklyGrid(pitchId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ API dành cho ADMIN_SYSTEM:
     * Update / create giá cho 1 cell cụ thể
     */
    @PatchMapping("/cell")
    public ResponseEntity<UpdateBasePriceCellResponse> updateBasePriceCell(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @Validated @RequestBody UpdateBasePriceCellRequest request
    ) {
        UUID adminSystemId = userDetails.getId();

        log.info(
                "✏️ [ADMIN SYSTEM API] AdminSystem {} update base price cell: pitchId={}, dayOfWeek={}, {} -> {}, price={}",
                adminSystemId,
                request.getPitchId(),
                request.getDayOfWeek(),
                request.getTimeStart(),
                request.getTimeEnd(),
                request.getPrice()
        );

        UpdateBasePriceCellResponse response =
                basePriceService.updateBasePriceCell(request);

        return ResponseEntity.ok(response);
    }
}
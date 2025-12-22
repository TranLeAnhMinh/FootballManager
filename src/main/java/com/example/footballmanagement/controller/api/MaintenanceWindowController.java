package com.example.footballmanagement.controller.api;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.dto.request.MaintenanceWindowRequest;
import com.example.footballmanagement.dto.response.BookingOverlapResponse;
import com.example.footballmanagement.dto.response.MaintenanceWindowResponse;
import com.example.footballmanagement.service.MaintenanceWindowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pitches/{pitchId}/maintenance-windows")
@RequiredArgsConstructor
public class MaintenanceWindowController {
    private final MaintenanceWindowService maintenanceWindowService;

    @GetMapping
    public ResponseEntity<List<MaintenanceWindowResponse>> getAllByPitch(
        @PathVariable UUID pitchId,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
    // ✅ Controller chỉ gọi service, không if/else
    return ResponseEntity.ok(
            maintenanceWindowService.getMaintenanceWindows(pitchId, from, to)
    );
    }

    @PostMapping
    public ResponseEntity<MaintenanceWindowResponse> createMaintenanceWindow(
        @AuthenticationPrincipal JwtUserDetails userDetails,
        @RequestBody MaintenanceWindowRequest req
    ){
        UUID adminId = userDetails.getId();
        MaintenanceWindowResponse response = maintenanceWindowService.createMaintenanceWindow(adminId, req);
        return ResponseEntity.ok(response);
    }
     /** ✅ Kiểm tra trùng booking */
    @GetMapping("/check-overlap")
    public ResponseEntity<Map<String, Object>> checkOverlap(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @PathVariable UUID pitchId,
            @RequestParam OffsetDateTime startAt,
            @RequestParam OffsetDateTime endAt
    ) {
        List<BookingOverlapResponse> overlaps =
                maintenanceWindowService.checkOverlap(pitchId, startAt, endAt);

        Map<String, Object> res = new HashMap<>();
        res.put("conflict", !overlaps.isEmpty());
        res.put("overlaps", overlaps);
        return ResponseEntity.ok(res);
    }



}

package com.example.footballmanagement.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.footballmanagement.constant.Endpoint.BRANCH_ADMIN_SYSTEM_API_BASE;
import com.example.footballmanagement.dto.request.BranchCreateRequest;
import com.example.footballmanagement.dto.response.BranchResponse;
import com.example.footballmanagement.dto.response.BranchResponseDto;
import com.example.footballmanagement.service.BranchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(BRANCH_ADMIN_SYSTEM_API_BASE)
@RequiredArgsConstructor
public class BranchAdminSystemController {

    private final BranchService branchService;

    // ===========================
    // 🔹 API: TẠO CHI NHÁNH MỚI
    // ===========================
    @PostMapping
    public ResponseEntity<BranchResponseDto> createBranch(
            @RequestBody BranchCreateRequest request
    ) {
        BranchResponseDto response = branchService.createBranch(request);
        return ResponseEntity.ok(response);
    }

    // ===========================================
    // 🔹 API: LẤY DANH SÁCH CHI NHÁNH + CÁC SÂN
    // ===========================================
    @GetMapping
    public ResponseEntity<List<BranchResponse>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranchesWithPitches());
    }
}

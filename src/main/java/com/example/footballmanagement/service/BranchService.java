package com.example.footballmanagement.service;


import java.util.List;

import com.example.footballmanagement.dto.request.BranchCreateRequest;
import com.example.footballmanagement.dto.response.BranchResponse;
import com.example.footballmanagement.dto.response.BranchResponseDto;

public interface BranchService {
    BranchResponseDto createBranch(BranchCreateRequest request);

    List<BranchResponse> getAllBranchesWithPitches();
}

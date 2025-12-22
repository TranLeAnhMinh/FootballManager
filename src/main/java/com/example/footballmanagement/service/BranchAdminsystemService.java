package com.example.footballmanagement.service;

import java.util.List;
import java.util.UUID;

import com.example.footballmanagement.entity.Branch;

public interface BranchAdminsystemService {
    void approvePendingAdmin(UUID adminSystemId, UUID userId, UUID branchId);
    List<Branch> getAvailableBranches();
}

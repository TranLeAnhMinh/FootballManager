package com.example.footballmanagement.controller.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.config.JwtUserDetails;
import com.example.footballmanagement.dto.response.VoucherResponse; // class bạn đang dùng cho token
import com.example.footballmanagement.service.VoucherService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {
    
    private final VoucherService voucherService;

    //Lấy danh sách voucher khả dụng cho user đã login
    @GetMapping("/available")
    public ResponseEntity<List<VoucherResponse>> getAvailableVouchers(
        @AuthenticationPrincipal JwtUserDetails userDetails
    ){
        UUID userId = userDetails.getId();
        List<VoucherResponse> vouchers = voucherService.getAvailableVouchersForUser(userId);
        return ResponseEntity.ok(vouchers);
    }
    
}

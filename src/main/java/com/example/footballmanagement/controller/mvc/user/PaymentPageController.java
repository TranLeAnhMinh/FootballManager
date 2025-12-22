package com.example.footballmanagement.controller.mvc.user;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.footballmanagement.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentPageController {

    private final PaymentService paymentService;

    /**
     * Tạo URL thanh toán VNPAY cho booking và redirect
     */
    @PostMapping("/{bookingId}")
    public ResponseEntity<Void> createPayment(
            @PathVariable UUID bookingId,
            HttpServletRequest request) throws Exception {

        // Gọi service tạo payment URL
        String url = paymentService.createPaymentUrl(bookingId, request);

        // Trả về redirect sang VNPAY
        return ResponseEntity.status(302)
                .header("Location", url)
                .build();
    }
}
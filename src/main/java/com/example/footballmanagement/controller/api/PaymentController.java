package com.example.footballmanagement.controller.api;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;          // ✅ thêm
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // ✅ thêm
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.footballmanagement.entity.Payment;
import com.example.footballmanagement.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/vnpay")
    @ResponseBody // ✅ Chỉ riêng endpoint này trả JSON
    public ResponseEntity<String> createPaymentUrl(
            @RequestParam UUID bookingId,
            HttpServletRequest request) throws Exception {
        String url = paymentService.createPaymentUrl(bookingId, request);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/vnpay-return")
    public String handleVnPayReturn(@RequestParam Map<String, String> allParams,
                                    RedirectAttributes redirectAttributes) throws Exception {
        Payment payment = paymentService.handleVnPayReturn(allParams);

        if (payment.getStatus().name().equals("PAID")) {
            redirectAttributes.addFlashAttribute("successMessage", "Đặt sân thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán thất bại, vui lòng thử lại.");
        }

        return "redirect:/user/home"; // ✅ Redirect thật sự
    }
}

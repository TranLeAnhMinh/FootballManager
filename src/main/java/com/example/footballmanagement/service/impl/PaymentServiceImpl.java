package com.example.footballmanagement.service.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.footballmanagement.config.VnPayConfig;
import com.example.footballmanagement.entity.Booking;
import com.example.footballmanagement.entity.Payment;
import com.example.footballmanagement.entity.enums.BookingStatus;
import com.example.footballmanagement.entity.enums.PaymentStatus;
import com.example.footballmanagement.repository.BookingRepository;
import com.example.footballmanagement.repository.PaymentRepository;
import com.example.footballmanagement.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final VnPayConfig vnPayConfig;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;


    @Override
@Transactional
public String createPaymentUrl(UUID bookingId, HttpServletRequest request) throws Exception {
    // ✅ Lấy booking trực tiếp từ DB
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

    BigDecimal finalPrice = booking.getFinalPrice();
    if (finalPrice == null || finalPrice.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalStateException("Final price is not available for booking " + bookingId);
    }

    // ==== Build VNPAY params ====
    Map<String, String> params = new HashMap<>();
    params.put("vnp_Version", "2.1.0");
    params.put("vnp_Command", "pay");
    params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
    params.put("vnp_Amount", String.valueOf(finalPrice.multiply(BigDecimal.valueOf(100)).longValue()));
    params.put("vnp_CurrCode", "VND");
    params.put("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));
    params.put("vnp_OrderInfo", "Thanh toan booking " + booking.getId());
    params.put("vnp_OrderType", "other");
    params.put("vnp_Locale", "vn");
    params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
    params.put("vnp_IpAddr", request.getRemoteAddr());
    params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

    // ==== Build query + hash ====
    List<String> fieldNames = new ArrayList<>(params.keySet());
    Collections.sort(fieldNames);

    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();

    for (int i = 0; i < fieldNames.size(); i++) {
        String name = fieldNames.get(i);
        String value = params.get(name);
        if (value != null && !value.isEmpty()) {
            hashData.append(name).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            query.append(name).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            if (i < fieldNames.size() - 1) {
                hashData.append('&');
                query.append('&');
            }
        }
    }

    String secureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
    query.append("&vnp_SecureHash=").append(secureHash);

    return vnPayConfig.getPayUrl() + "?" + query;
}


    @Override
    @Transactional
    public Payment handleVnPayReturn(Map<String, String> vnpayParams) throws Exception {
        String vnp_SecureHash = vnpayParams.get("vnp_SecureHash");
        vnpayParams.remove("vnp_SecureHash");
        vnpayParams.remove("vnp_SecureHashType");

        // ✅ verify checksum
        String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), buildHashData(vnpayParams));
        if (!calculatedHash.equals(vnp_SecureHash)) {
            throw new RuntimeException("Invalid checksum from VNPAY");
        }

        // ✅ lấy bookingId từ OrderInfo
        String orderInfo = vnpayParams.get("vnp_OrderInfo"); // "Thanh toan booking {uuid}"
        UUID bookingId = UUID.fromString(orderInfo.replace("Thanh toan booking ", ""));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        BigDecimal amount = new BigDecimal(vnpayParams.get("vnp_Amount"))
                .divide(BigDecimal.valueOf(100));

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(amount)
                .method("VNPAY")
                .status("00".equals(vnpayParams.get("vnp_ResponseCode")) ? PaymentStatus.PAID : PaymentStatus.FAILED)
                .build();

        if (payment.getStatus() == PaymentStatus.PAID) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(booking);
        }

        return paymentRepository.save(payment);
    }

    private String buildHashData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String name = fieldNames.get(i);
            String value = params.get(name);
            if (value != null && !value.isEmpty()) {
                hashData.append(name).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                if (i < fieldNames.size() - 1) {
                    hashData.append('&');
                }
            }
        }
        return hashData.toString();
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
        throw new RuntimeException("Error while generating HMAC SHA512 hash", e);
    }
    }
}

package com.example.footballmanagement.service;

import java.util.Map;
import java.util.UUID;

import com.example.footballmanagement.entity.Payment;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    String createPaymentUrl(UUID bookingId, HttpServletRequest request) throws Exception;
    Payment handleVnPayReturn(Map<String, String> params) throws Exception;
}

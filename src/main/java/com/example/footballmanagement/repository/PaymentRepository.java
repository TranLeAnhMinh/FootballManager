package com.example.footballmanagement.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.footballmanagement.entity.Payment;


public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}

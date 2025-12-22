package com.example.footballmanagement.service;

public interface NotificationService {
    void sendSimpleMessage(String to, String subject, String text);
}

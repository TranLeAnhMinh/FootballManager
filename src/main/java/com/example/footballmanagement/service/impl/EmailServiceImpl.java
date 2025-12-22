package com.example.footballmanagement.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.footballmanagement.service.NotificationService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements NotificationService {

    private final JavaMailSender emailSender;
    @Async      
    @Override
    public void sendSimpleMessage(String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
    
}

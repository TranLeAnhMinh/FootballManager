package com.example.footballmanagement.exception.custom;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class RegisterExceptionHandler {

    @ExceptionHandler(RegisterException.class)
    public String handleRegisterException(RegisterException ex, RedirectAttributes redirectAttributes) {
        // flash attribute cho Thymeleaf
        redirectAttributes.addFlashAttribute("registerError", ex.getMessage());
        return "redirect:/register"; // quay lại form register
    }
}

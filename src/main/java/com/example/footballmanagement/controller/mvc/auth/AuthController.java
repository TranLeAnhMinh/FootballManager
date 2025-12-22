package com.example.footballmanagement.controller.mvc.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.footballmanagement.dto.request.RegisterRequest;
import com.example.footballmanagement.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @GetMapping("/login")
    public String loginPage() {
        return "login"; 
    }
    
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request, RedirectAttributes redirectAttributes) {
        userService.register(request);
        redirectAttributes.addFlashAttribute("registerSuccess", true);
        return "redirect:/login"; // sau khi đăng ký thành công -> chuyển sang trang login
    }

    @GetMapping("/forgotpassword")
    public String forgotPasswordPage() {
        return "forgotpassword"; 
    }

    @GetMapping("/resetpassword")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "resetpassword"; // trả về file resetpassword.html
    }
}

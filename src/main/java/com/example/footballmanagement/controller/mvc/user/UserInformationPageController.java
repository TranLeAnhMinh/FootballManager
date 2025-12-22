package com.example.footballmanagement.controller.mvc.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserInformationPageController {

    @GetMapping("/user/userinformation")
    public String userInformationPage(Model model) {
        // ✅ Gửi biến cho sidebar biết trang đang active
        model.addAttribute("activePage", "userinformation");
        return "user/userinformation"; // render file userinformation.html
    }
}

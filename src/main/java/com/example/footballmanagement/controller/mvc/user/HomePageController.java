package com.example.footballmanagement.controller.mvc.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {

    @GetMapping("/user/home")
    public String homePage(Model model) {
        model.addAttribute("activePage", "home");
        return "user/homepage"; 
    }
}

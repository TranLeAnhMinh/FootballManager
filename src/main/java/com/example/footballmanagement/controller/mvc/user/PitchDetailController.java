package com.example.footballmanagement.controller.mvc.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PitchDetailController {

    @GetMapping("/user/pitch/{id}")
    public String pitchDetailPage(@PathVariable String id, Model model) {
        model.addAttribute("pitchId", id); // Truyền pitchId xuống Thymeleaf
        return "user/pitchdetailpage";     // => resources/templates/user/pitchdetailpage.html
    }
}
package com.example.footballmanagement.controller.mvc.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomePageAdminController {

    @GetMapping("/admin/home")
    public String adminHomePage(Model model, HttpServletRequest request) {
        // ✅ Dùng cùng convention với trang user
        model.addAttribute("activePage", "adminHome");
        model.addAttribute("currentPath", request.getRequestURI());
        // ✅ Trả về view tương ứng (giống user/homepage)
        return "admin/adminpage";
    }
}

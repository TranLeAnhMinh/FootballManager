package com.example.footballmanagement.controller.mvc.adminsystem;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomePageAdminsystemController {

    @GetMapping("/adminsystem/home")
    public String adminSystemHomePage(Model model, HttpServletRequest request) {
        // ✅ Xác định trang hiện tại để đánh dấu active trong sidebar
        model.addAttribute("activePage", "systemHome");

        // ✅ Lưu đường dẫn hiện tại để langswitcher dùng chuyển đổi ngôn ngữ đúng URL
        model.addAttribute("currentPath", request.getRequestURI());

        // ✅ Trả về view tương ứng (templates/adminsystem/adminsystempage.html)
        return "adminsystem/adminsystempage";
    }

}

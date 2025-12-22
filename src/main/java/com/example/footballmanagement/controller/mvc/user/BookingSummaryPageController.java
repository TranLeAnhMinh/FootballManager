package com.example.footballmanagement.controller.mvc.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class BookingSummaryPageController {

    @GetMapping("/bookingsummary")
    public String bookingSummaryPage(Model model) {
        // ✅ Gửi biến cho sidebar biết đang ở trang booking summary
        model.addAttribute("activePage", "bookingsummary");

        // Trả về template tĩnh: templates/user/bookingsummarypage.html
        return "user/bookingsummarypage";
    }
}

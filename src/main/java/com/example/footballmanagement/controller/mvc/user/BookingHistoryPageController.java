package com.example.footballmanagement.controller.mvc.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BookingHistoryPageController {

    @GetMapping("/user/bookinghistory")  // nhớ thêm dấu "/" trước user
    public String bookingHistoryPage(Model model) {
        // ✅ Gửi biến để sidebar biết trang nào đang active
        model.addAttribute("activePage", "bookinghistory");
        return "user/bookinghistory";
    }
}

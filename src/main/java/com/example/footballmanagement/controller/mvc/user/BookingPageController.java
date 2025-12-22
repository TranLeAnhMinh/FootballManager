package com.example.footballmanagement.controller.mvc.user;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/booking")
public class BookingPageController {

    @GetMapping("/{pitchId}")
    public String bookingPage(@PathVariable UUID pitchId, Model model) {
        model.addAttribute("pitchId", pitchId);

        // ✅ Gửi biến để sidebar biết đang ở trang booking
        model.addAttribute("activePage", "booking");

        return "user/booking"; // => templates/user/booking.html
    }
}

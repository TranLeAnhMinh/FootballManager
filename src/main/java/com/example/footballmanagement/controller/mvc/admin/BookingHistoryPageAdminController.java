package com.example.footballmanagement.controller.mvc.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookingHistoryPageAdminController {

    @GetMapping("/admin/bookings")
    public String bookingHistoryPage(Model model) {
        // ✅ set biến activePage để sidebar highlight menu "Bookings"
        model.addAttribute("activePage", "bookings");

        // ✅ trả về view: templates/admin/branch-booking-history.html
        return "admin/branch-booking-history";
    }
}

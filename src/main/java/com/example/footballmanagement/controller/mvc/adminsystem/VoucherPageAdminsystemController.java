package com.example.footballmanagement.controller.mvc.adminsystem;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class VoucherPageAdminsystemController {

    @GetMapping("/adminsystem/vouchers")
    public String adminSystemVoucherPage(Model model, HttpServletRequest request) {

        // đánh dấu active sidebar
        model.addAttribute("activePage", "voucherManagement");

        // phục vụ langswitcher
        model.addAttribute("currentPath", request.getRequestURI());

        // trả về html (templates/adminsystem/adminsystem-voucher.html)
        return "adminsystem/adminsystem-voucher";
    }
}

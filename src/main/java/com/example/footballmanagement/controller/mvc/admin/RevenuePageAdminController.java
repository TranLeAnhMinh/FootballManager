package com.example.footballmanagement.controller.mvc.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RevenuePageAdminController {

    @GetMapping("/admin/revenue")
    public String branchRevenuePage(Model model, HttpServletRequest request) {
         System.out.println("✅ [CONTROLLER] /admin/revenue reached successfully");
        // ✅ Gắn biến để sidebar highlight đúng menu "Doanh thu"
        model.addAttribute("activePage", "revenue");
        
        // ✅ Lưu path hiện tại để dùng trong template (nếu cần)
        model.addAttribute("currentPath", request.getRequestURI());
        
        // ✅ Trả về view: templates/admin/revenue.html
        return "admin/revenue";
    }
}

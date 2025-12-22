package com.example.footballmanagement.controller.mvc.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MaintenanceHistoryPageAdminController {

    @GetMapping("/admin/maintenance/history")
    public String maintenanceHistoryPage(Model model) {
        // ✅ set biến activePage để sidebar highlight menu "Maintenance"
        model.addAttribute("activePage", "maintenancehistory");

        // ✅ trả về view: templates/admin/branch-maintenance-history.html
        return "admin/branch-maintenance-history";
    }
}

package com.example.footballmanagement.controller.mvc.admin;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MaintenancePageAdminController {

    @GetMapping("/admin/maintenance/{pitchId}")
    public String maintenancePage(
            @PathVariable UUID pitchId,
            Model model
    ) {
        // ✅ Truyền pitchId sang view để JS (maintenance.js) sử dụng
        model.addAttribute("pitchId", pitchId);
        model.addAttribute("activePage", "maintenance");

        // ✅ Trả về file templates/admin/maintenance.html
        return "admin/maintenance";
    }
}

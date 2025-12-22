package com.example.footballmanagement.controller.mvc.adminsystem;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BranchPageAdminsystemController {

    @GetMapping("/adminsystem/branches")
    public String adminSystemBranchPage(Model model, HttpServletRequest request) {

        // đánh dấu active sidebar
        model.addAttribute("activePage", "branchManagement");

        // phục vụ langswitcher
        model.addAttribute("currentPath", request.getRequestURI());

        // trả về html (templates/adminsystem/branch/branchpage.html)
        return "adminsystem/adminsystembranchpage";
    }
}

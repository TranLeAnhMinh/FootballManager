package com.example.footballmanagement.controller.mvc.adminsystem;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PitchDetailAdminsystemPageController {

    @GetMapping("/adminsystem/pitches/{pitchId}")
    public String adminSystemPitchDetailPage(
            @PathVariable("pitchId") UUID pitchId,
            Model model,
            HttpServletRequest request
    ) {

        // đánh dấu active sidebar
        model.addAttribute("activePage", "pitchManagement");

        // truyền pitchId cho FE JS
        model.addAttribute("pitchId", pitchId);

        // phục vụ langswitcher
        model.addAttribute("currentPath", request.getRequestURI());

        return "adminsystem/adminsystem-pitch-detail";
    }
}

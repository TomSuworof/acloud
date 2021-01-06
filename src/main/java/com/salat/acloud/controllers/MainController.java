package com.salat.acloud.controllers;

import com.salat.acloud.entities.Role;
import com.salat.acloud.entities.User;
import com.salat.acloud.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;

    @GetMapping("/")
    public String returnStartPage(Model model) {
        User currentUser = userService.getUserFromContext();
        if (currentUser != null) {
            if (currentUser.getRoles().contains(new Role((long) 1, "ROLE_ADMIN"))) {
                model.addAttribute("show_admin_page", true);
            }
//            } else if (currentUser.getRoles().contains(new Role((long) 2, "ROLE_ANALYST"))) {
//                model.addAttribute("show_analyst_page", true);
//            }
            model.addAttribute("show_personal_area", true);
            model.addAttribute("show_logout", true);
        }
        return "index";
    }

    @GetMapping("/login")
    public String returnLogin() {
        return "login";
    }

    @GetMapping("/terms_of_use")
    String getTerms() {
        return "terms_of_use";
    }
}

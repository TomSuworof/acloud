package com.salat.acloud.controllers;

import com.salat.acloud.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/admin")
    public String returnAdminPage(Model model) {
        model.addAttribute("allUsers", userService.getAllUsers());
        return "admin";
    }

    @PostMapping("/admin")
    public String changeRole(@RequestParam String action, @RequestParam Long userId, Model model) {
        if(switch(action) {
            case "delete" -> userService.changeRole(userId, "blocked");
            case "make_analyst" -> userService.changeRole(userId, "analyst");
            case "make_user" -> userService.changeRole(userId, "user");
            default -> false;
        }) {
            return "redirect:/admin";
        } else {
            model.addAttribute("error", "Error changing role");
            return "admin";
        }
    }
}

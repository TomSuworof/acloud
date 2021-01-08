package com.salat.acloud.controllers;

import com.salat.acloud.entities.User;
import com.salat.acloud.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;

    @GetMapping("/")
    public String getStartPage() {
        User currentUser = userService.getUserFromContext();
        if (currentUser != null) {
            return "redirect:/dashboard";
        }
        return "index";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/terms_of_use")
    String getTerms() {
        return "terms_of_use";
    }
}

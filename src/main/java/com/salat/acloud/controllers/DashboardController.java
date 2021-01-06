package com.salat.acloud.controllers;

import com.salat.acloud.entities.User;
import com.salat.acloud.services.UserFileService;
import com.salat.acloud.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;
    private final UserFileService userFileService;

    @GetMapping("/dashboard")
    String getDashboard(Model model) {
        User currentUser = userService.getUserFromContext();
        model.addAttribute("files", currentUser.getUserFiles());
        return "dashboard";
    }

    @GetMapping("/dashboard/load_file")
    String loadFile(@RequestParam File loadedFile, Model model) {
        User currentUser = userService.getUserFromContext();
        if (userFileService.loadFile(currentUser, loadedFile)) {
            return "redirect: /dashboard";
        } else {
            model.addAttribute("error", "Error loading file");
            return "dashboard";
        }
    }
}

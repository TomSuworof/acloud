package com.salat.acloud.controllers;

import com.salat.acloud.entities.User;
import com.salat.acloud.entities.UserFile;
import com.salat.acloud.services.SearchService;
import com.salat.acloud.services.UserFileService;
import com.salat.acloud.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;
    private final UserFileService userFileService;
    private final SearchService searchService;

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        User currentUser = userService.getUserFromContext();
        model.addAttribute("files", currentUser.getUserFiles());
        return "dashboard";
    }

    @PostMapping("/dashboard/load_file")
    public String loadFile(@RequestParam(name = "file") MultipartFile loadedFile, Model model) {
        User currentUser = userService.getUserFromContext();
        if (userFileService.loadFileTo(loadedFile, currentUser)) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Error loading file");
            return "dashboard";
        }
    }

    @GetMapping("/dashboard/search")
    public String getSearchResults(@RequestParam String query, Model model) {
        try {
            List<UserFile> userFilesByQuery = searchService.getFilesByQuery(query);
            model.addAttribute("files", userFilesByQuery);
            return "dashboard";
        } catch (FileNotFoundException noFile) {
            model.addAttribute("error", "Something went wrong");
            return "dashboard";
        }
    }
}

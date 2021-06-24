package com.salat.acloud.controllers;

import com.salat.acloud.entities.UserFile;
import com.salat.acloud.services.UserFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequiredArgsConstructor
public class DownloadController {
    private final UserFileService userFileService;

    @GetMapping("/download_page/{id}")
    public String getDownloadPage(@PathVariable Long id, Model model) {
        try {
            UserFile responseFile = userFileService.getSomeFileById(id);
            model.addAttribute("userFile", responseFile);
            return "download_page";
        } catch (FileNotFoundException e) {
            return "no_rights";
        }
    }

    @GetMapping("/download_my/{id}")
    public @ResponseBody Object getMyFile(@PathVariable Long id, HttpServletResponse response) {
        try {
            UserFile responseFile = userFileService.getMyFileById(id);
            response.setContentType("application/" + responseFile.getExtension());
            response.setHeader("Content-Disposition", "inline; filename=" + responseFile.getFilename());
            response.setHeader("Content-Length", String.valueOf(responseFile.makeFile().length()));
            return new FileSystemResource(responseFile.makeFile());
        } catch (FileNotFoundException e) {
            return "You have no rights";
        }
    }

    @GetMapping("/download/{id}")
    public @ResponseBody Object getSomeFile(@PathVariable Long id, HttpServletResponse response) {
        try {
            UserFile responseFile = userFileService.getSomeFileById(id);
            response.setContentType("application/" + responseFile.getExtension());
            response.setHeader("Content-Disposition", "inline; filename=" + responseFile.getFilename());
            response.setHeader("Content-Length", String.valueOf(responseFile.makeFile().length()));
            return new FileSystemResource(responseFile.makeFile());
        } catch (FileNotFoundException e) {
            return "You have no rights";
        }
    }

    @GetMapping("/download_my/{mode}/{id}")
    public String getPermissionForFile(@PathVariable String mode, @PathVariable Long id, Model model) {
        if (userFileService.setVisibility(id, mode)) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Error changing state");
            return "dashboard";
        }
    }
}

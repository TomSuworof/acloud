package com.salat.acloud.controllers;

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

    @GetMapping("/download_my/{id}")
    public @ResponseBody Object getMyFile(@PathVariable Long id, HttpServletResponse response) {
        try {
            File responseFile = userFileService.getMyFileById(id).makeFile();
            response.setContentType("application/" + userFileService.getContentType(responseFile));
            response.setHeader("Content-Disposition", "inline; filename=" + responseFile.getName());
            response.setHeader("Content-Length", String.valueOf(responseFile.length()));
            return new FileSystemResource(responseFile);
        } catch (FileNotFoundException noFile) {
            return "You have no rights";
        }
    } // todo replace by page 'no_rights'

    @GetMapping("/download/{id}")
    public @ResponseBody Object getSomeFile(@PathVariable Long id, HttpServletResponse response) {
        try {
            File responseFile = userFileService.getSomeFileById(id).makeFile();
            response.setContentType("application/" + userFileService.getContentType(responseFile));
            response.setHeader("Content-Disposition", "inline; filename=" + responseFile.getName());
            response.setHeader("Content-Length", String.valueOf(responseFile.length()));
            return new FileSystemResource(responseFile);
        } catch (FileNotFoundException e) {
            return "You have no rights";
        }
    } // todo replace by page 'no_rights'

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

package com.salat.acloud.controllers;

import com.salat.acloud.services.UserFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
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
            File responseFile = userFileService.getMyFileById(id);
            response.setContentType("application/" + userFileService.getExtension(responseFile));
            response.setHeader("Content-Disposition", "inline; filename=" + responseFile.getName());
            response.setHeader("Content-Length", String.valueOf(responseFile.length()));
            return new FileSystemResource(responseFile);
        } catch (FileNotFoundException noFile) {
            return "You have no rights"; // todo replace by page 'no_rights'
        }
    }

//    @GetMapping("/download/{id}")
//    public @ResponseBody Object getSomeFile(@PathVariable Long id, HttpServletResponse response) {
//        try {
//            File responseFile = userFileService.getSomeFileById(id);
//            response.setContentType("application/" + userFileService.getExtension(responseFile));
//            response.setHeader("Content-Disposition", "inline; filename=" + responseFile.getName());
//            response.setHeader("Content-Length", String.valueOf(responseFile.length()));
//            return new FileSystemResource(responseFile);
//        } catch (FileNotFoundException noFile) {
//            return "You have no rights"; // todo replace by page 'no_rights'
//        }
//    }

    @GetMapping("/download/{id}")
    public void getSomeFile(@PathVariable Long id, HttpServletResponse response) {
        try {
            File responseFile = userFileService.getSomeFileById(id);
            InputStream inputStream = new FileInputStream(responseFile);
            FileCopyUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // todo fix this
}

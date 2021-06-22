package com.salat.acloud.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/api")
    public String getStartPage() {
        return "Not working";
    }
}

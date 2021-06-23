package com.salat.acloud.api;

import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WebController {

    @GetMapping("/api")
    public ResponseEntity<String> getStartPage() {
        Map<String, Object> response = new HashMap<>();
        response.put("number", (int) (Math.random() * 10));
        response.put("msg", "This shit is working");
        return ResponseEntity.ok().body(new Gson().toJson(response));
    }
}

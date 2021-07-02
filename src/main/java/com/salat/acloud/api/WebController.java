package com.salat.acloud.api;

import com.google.gson.Gson;
import com.salat.acloud.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebController {
    private final ClientService clientService;

    @GetMapping("/api")
    public ResponseEntity<String> getStartPage(
            @RequestParam Long clientId,
            @RequestParam String clientName,
            @RequestParam String clientSecret
    ) {
        Map<String, Object> response = new HashMap<>();
        if (clientService.isClientValid(clientId, clientName, clientSecret)) {
            System.out.println("The client exists");
            response.put("number", (int) (Math.random() * 10));
            response.put("msg", "This shit is working");
        } else {
            System.out.println("The client does not exist");
            response.put("number", 0);
            response.put("msg", "Wrong credentials");
        }
        return ResponseEntity.ok().body(new Gson().toJson(response));
    }
}

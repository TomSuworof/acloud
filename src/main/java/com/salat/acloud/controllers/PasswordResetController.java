package com.salat.acloud.controllers;

import com.salat.acloud.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @GetMapping("/password_reset")
    public String returnPasswordResetRequestPage() {
        return "password_reset";
    }

    @GetMapping("/password_reset/send")
    public String sendPasswordResetRequest(@RequestParam String username, Model model) {
        if (passwordResetService.send(username)) {
            model.addAttribute("message", "Message was sent to " + passwordResetService.getEmail());
            model.addAttribute("note", "If you do not see it, check Spam");
        } else {
            model.addAttribute("message", "User with this username does not exist");
        }
        return "password_reset";
    }

    @GetMapping("/password_reset/change_password/{id}")
    public String checkValidity(@PathVariable(name = "id") String id, Model model) {
        if (passwordResetService.isRequestValid(id)) {
            model.addAttribute("secretQuestion", passwordResetService.getSecretQuestion(id));
            model.addAttribute("id", id);
            return "password_reset_new_password";
        } else {
            return "password_reset_cancelled";
        }
    }

    @PostMapping("password_reset/change_password/set_password")
    public String setNewPassword(@RequestParam String passwordNew,
                                 @RequestParam String passwordNewConfirm,
                                 @RequestParam String id,
                                 @RequestParam String secretAnswer,
                                 Model model) {
        if (!secretAnswer.equals(passwordResetService.getSecretAnswer(id))) {
            model.addAttribute("message", "Wrong answer");
            model.addAttribute("id", id);
            return "password_reset_new_password";
        }
        if (!passwordNew.equals(passwordNewConfirm)) {
            model.addAttribute("message", "Passwords do not match");
            model.addAttribute("id", id);
            return "password_reset_new_password";
        }
        if (passwordResetService.setNewPassword(passwordNew, id)) {
            return "redirect:/";
        } else {
            return "password_reset_cancelled";
        }
    }
}

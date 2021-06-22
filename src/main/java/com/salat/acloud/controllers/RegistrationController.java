package com.salat.acloud.controllers;

import com.salat.acloud.entities.User;
import com.salat.acloud.services.MailService;
import com.salat.acloud.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final MailService mailService;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("userForm") User userForm,
                          String agreement,
                          BindingResult bindingResult,
                          Model model) {
        if (agreement == null) {
            model.addAttribute("agreementError", "In order to use service you should agree with terms of use");
            return "registration";
        }
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            model.addAttribute("passwordError", "Passwords do not match");
            return "registration";
        }
        if (userService.saveUser(userForm)){
            mailService.send(userForm.getEmail(), "registration_confirm", userForm.getUsername());
            return "redirect:/";
        } else {
            model.addAttribute("usernameError", "Username unavailable");
            return "registration";
        }
    }
}

package com.salat.acloud.services;


import com.salat.acloud.entities.PasswordResetRequest;
import com.salat.acloud.entities.User;
import com.salat.acloud.repositories.PasswordResetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserService userService;
    private final PasswordResetRepository passwordResetRepository;
    private final MailService mailService;

    private String email;

    public boolean send(String username) {
        try {
            String id = UUID.randomUUID().toString();
            Date created = new Date();
            User requiredUser = (User) userService.loadUserByUsername(username);
            email = requiredUser.getEmail();
            passwordResetRepository.save(new PasswordResetRequest(id, username, created));

            String link = "https://acloud.herokuapp.com/password_reset/change_password/" + id;

            return mailService.send(email, "password_change", link);

        } catch (UsernameNotFoundException usernameNotFoundException) {
            return false;
        }
    }

    public boolean isRequestValid(String id) {
        Optional<PasswordResetRequest> request = passwordResetRepository.findById(id);
        if(request.isPresent()) {
            Calendar now = new GregorianCalendar();
            Date dateCreated = request.get().getCreated();
            Calendar dateExpired = new GregorianCalendar();
            dateExpired.setTime(dateCreated);
            dateExpired.add(Calendar.HOUR, 24);
            return now.before(dateExpired);
        } else {
            return false;
        }
    }

    public String getEmail() {
        return hideEmail(email);
    }

    private String hideEmail(String email) {
        StringBuilder emailAnswer = new StringBuilder();
        String emailName = email.split("@")[0];
        String emailDomain = email.split("@")[1];
        emailAnswer.append(emailName.charAt(0));
        emailAnswer.append("*".repeat(emailName.length()));
        emailAnswer.append(emailDomain);
        return emailAnswer.toString();
    }

    public boolean setNewPassword(String passwordNew, String id) {
        try {
            Optional<PasswordResetRequest> passwordResetRequest = passwordResetRepository.findById(id);

            if (passwordResetRequest.isEmpty()) {
                return false;
            }

            User userForNewPassword = (User) userService.loadUserByUsername(passwordResetRequest.get().getUsername());
            userForNewPassword.setPasswordNew(passwordNew);
            passwordResetRepository.delete(passwordResetRequest.get());
            return userService.updateUser(userForNewPassword, true);
        } catch (UsernameNotFoundException usernameNotFoundException) {
            return false;
        }
    }

    public String getSecretQuestion(String id) {
        Optional<PasswordResetRequest> passwordResetRequest = passwordResetRepository.findById(id);

        if (passwordResetRequest.isPresent()) {
            User userForNewPassword = (User) userService.loadUserByUsername(passwordResetRequest.get().getUsername());
            return userForNewPassword.getSecretQuestion();
        } else {
            throw new NullPointerException();
        }
    }

    public String getSecretAnswer(String id) {
        Optional<PasswordResetRequest> passwordResetRequest = passwordResetRepository.findById(id);

        if (passwordResetRequest.isPresent()) {
            User userForNewPassword = (User) userService.loadUserByUsername(passwordResetRequest.get().getUsername());
            return userForNewPassword.getSecretAnswer();
        } else {
            throw new NullPointerException();
        }
    }
}
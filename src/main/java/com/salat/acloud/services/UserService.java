package com.salat.acloud.services;

import com.salat.acloud.entities.Role;
import com.salat.acloud.entities.User;
import com.salat.acloud.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userFromDB = userRepository.findByUsername(username);
        if (userFromDB == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return userFromDB;
    }

    public User getUserFromContext() {
        Object currentUserDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUserDetails.equals("anonymousUser")) {
            return null;
        } else {
            return (User) currentUserDetails;
        }
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }
        int id = (user.getUsername() + user.getEmail()).hashCode();
        user.setId((long) id);
        user.setRoles(Collections.singleton(new Role(3L, "ROLE_USER")));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean updateUser(User userFromForm, boolean passwordWasChanged) {
        User userFromDB = userRepository.findById(userFromForm.getId()).orElseThrow(RuntimeException::new);

        if (!deleteUser(userFromForm.getId())) {
            return false;
        }

        userFromDB.setEmail(userFromForm.getEmail());
        userFromDB.setUserFiles(userFromForm.getUserFiles());

        if (passwordWasChanged) {
            return updateWithPassword(userFromDB, userFromForm.getPasswordNew());
        } else {
            return updateWithoutPassword(userFromDB);
        }
    }

    private boolean updateWithPassword(User userUpdated, String passwordNew) {
        userUpdated.setPassword(passwordEncoder.encode(passwordNew));
        userRepository.save(userUpdated);
        return true;
    }

    private boolean updateWithoutPassword(User userUpdated) {
        userRepository.save(userUpdated);
        return true;
    }

    private boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public boolean changeRole(Long userId, String role) {
        User userFromDB = userRepository.findById(userId).orElseThrow(RuntimeException::new);

        if (!deleteUser(userId)) {
            return false;
        }

        switch (role) {
            case "analyst" -> userFromDB.setRoles(Collections.singleton(new Role(2L, "ROLE_ANALYST")));
            case "user" -> userFromDB.setRoles(Collections.singleton(new Role(3L, "ROLE_USER")));
            case "blocked" -> userFromDB.setRoles(Collections.singleton(new Role(0L, "ROLE_BLOCKED")));
        }
//        mailService.send(userFromDB.getEmail(), "role_change", role);
        userRepository.save(userFromDB);
        return true;
    }

    public boolean isCurrentPasswordSameAs(String passwordAnother) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserPassword = currentUser.getPassword();
        return passwordEncoder.matches(passwordAnother, currentUserPassword);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean isUser(User user, String role) {
        if (role.equals("admin") && user.getRoles().contains(new Role((long) 1, "ROLE_ADMIN"))) {
            return true;
        } else if (role.equals("analyst") && user.getRoles().contains(new Role((long) 2, "ROLE_ANALYST"))) {
            return true;
        } else if (role.equals("user") && user.getRoles().contains(new Role((long) 3, "ROLE_USER"))) {
            return true;
        } else if (role.equals("blocked") && user.getRoles().contains(new Role((long) 0, "ROLE_BLOCKED"))) {
            return true;
        } else {
            return false;
        }
    }
}

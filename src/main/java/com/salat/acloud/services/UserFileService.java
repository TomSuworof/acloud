package com.salat.acloud.services;

import com.salat.acloud.entities.User;
import com.salat.acloud.entities.UserFile;
import com.salat.acloud.repositories.UserFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserFileService {
    private final UserService userService;
    private final UserFileRepository userFileRepository;

    public boolean loadFile(User user, File file) {
        UserFile userFile = new UserFile(file, user.getUsername());
        Set<UserFile> userFiles = user.getUserFiles();
        userFiles.add(userFile);
        user.setUserFiles(userFiles);
        return userService.saveUser(user);
    }
}

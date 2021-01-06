package com.salat.acloud.services;

import com.salat.acloud.entities.User;
import com.salat.acloud.entities.UserFile;
import com.salat.acloud.repositories.UserFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserFileService {
    private final UserService userService;
    private final UserFileRepository userFileRepository;

    public boolean loadFileTo(MultipartFile file, User user) {
        UserFile userFile = new UserFile(file, user.getUsername());
        Set<UserFile> userFiles = user.getUserFiles();
        userFiles.add(userFile);
        user.setUserFiles(userFiles);
        return saveFile(userFile) && userService.updateUser(user, false);
    }

    public boolean saveFile(UserFile userFile) {
        try {
            userFileRepository.save(userFile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
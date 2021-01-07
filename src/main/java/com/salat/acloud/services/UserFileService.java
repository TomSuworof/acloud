package com.salat.acloud.services;

import com.salat.acloud.entities.User;
import com.salat.acloud.entities.UserFile;
import com.salat.acloud.repositories.UserFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    private boolean saveFile(UserFile userFile) {
        try {
            userFileRepository.save(userFile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public File getMyFileById(Long id) throws FileNotFoundException {
        User currentUser = userService.getUserFromContext();
        List<UserFile> userFiles = currentUser.getUserFiles().stream()
                .filter(file -> file.getId().equals(id))
                .collect(Collectors.toList());
        if (userFiles.size() == 1) {
            return userFiles.get(0).makeFile();
        } throw new FileNotFoundException();
    }

    public File getSomeFileById(Long id) throws FileNotFoundException {
        Optional<UserFile> userFile = userFileRepository.findById(id);
        if (userFile.isPresent() && userFile.get().isCanBeDownloadedPublicly()) {
            return userFile.get().makeFile();
        } else {
            throw new FileNotFoundException();
        }
    }

    public String getExtension(File file) {
        String[] splitted = file.getName().split("\\.");
        return splitted[splitted.length - 1];
    }
}
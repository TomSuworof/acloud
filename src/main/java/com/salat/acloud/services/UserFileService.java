package com.salat.acloud.services;

import com.salat.acloud.entities.User;
import com.salat.acloud.entities.UserFile;
import com.salat.acloud.repositories.UserFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFileService {
    private final UserService userService;
    private final SearchService searchService;
    private final UserFileRepository userFileRepository;

    public boolean loadFileTo(MultipartFile file, User user) {
        UserFile userFile = new UserFile(file, user.getUsername());
        Set<UserFile> userFiles = user.getUserFiles();
        userFiles.add(userFile);
        user.setUserFiles(userFiles);
        return saveFile(userFile)
                && searchService.loadFileToIndex(userFile, user)
                && userService.updateUser(user, false);
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

    public boolean deleteFileFrom(Long id, User user) {
        try {
            UserFile userFileForDeleting = user.getUserFiles().stream()
                    .filter(file -> file.getId().equals(id))
                    .collect(Collectors.toList()).get(0);
            Set<UserFile> userFiles = user.getUserFiles();
            userFiles.remove(userFileForDeleting);
            user.setUserFiles(userFiles);
            return userService.updateUser(user, false)
                    && searchService.deleteFileFromIndex(userFileForDeleting, user)
                    && deleteFile(userFileForDeleting);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    private boolean deleteFile(UserFile userFile) {
        try {
            userFileRepository.delete(userFile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public UserFile getMyFileById(Long id) throws FileNotFoundException {
        User currentUser = userService.getUserFromContext();
        UserFile userFile;
        try {
            userFile = currentUser.getUserFiles().stream()
                    .filter(file -> file.getId().equals(id))
                    .collect(Collectors.toList())
                    .get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new FileNotFoundException();
        }
        return userFile;
    }

    public UserFile getSomeFileById(Long id) throws FileNotFoundException {
        Optional<UserFile> userFile = userFileRepository.findById(id);
        if (userFile.isPresent() && userFile.get().isCanBeDownloadedPublicly()) {
            return userFile.get();
        } else {
            throw new FileNotFoundException();
        }
    }

    public boolean setVisibility(Long id, String mode) {
        try {
            UserFile requiredFile = getMyFileById(id);
            switch (mode) {
                case "enable" -> requiredFile.setCanBeDownloadedPublicly(true);
                case "disable" -> requiredFile.setCanBeDownloadedPublicly(false);
            }
            return saveFile(requiredFile);
        } catch (FileNotFoundException noFile) {
            return false;
        }
    }
}
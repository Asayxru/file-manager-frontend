package com.filemanager.backend.service;

import com.filemanager.backend.dto.FolderDTO;
import com.filemanager.backend.entity.Folder;
import com.filemanager.backend.entity.User;
import com.filemanager.backend.repository.FolderRepository;
import com.filemanager.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class FolderService {

    private static final Logger logger = LoggerFactory.getLogger(FolderService.class);

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public FolderService(FolderRepository folderRepository, UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    public Optional<Folder> getFolderById(Long id) {
        logger.info("Fetching folder with ID: {}", id);
        return folderRepository.findById(id);
    }

    public List<Folder> getAllFolders() {
        logger.info("Fetching all folders");
        return folderRepository.findAll();
    }

    public List<Folder> getFoldersByOwner(String username) {
        logger.info("Fetching folders for user '{}'", username);
        User owner = findUserByUsername(username);
        return folderRepository.findByOwner(owner);
    }

    @Transactional
    public Folder saveFolder(FolderDTO folderDTO) {
        String username = getCurrentUsername();
        User owner = findUserByUsername(username);

        Folder folder = new Folder();
        folder.setName(folderDTO.getName());
        folder.setOwner(owner);

        if (folderDTO.getParentFolderId() != null) {
            Folder parent = folderRepository.findById(folderDTO.getParentFolderId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Parent folder with ID " + folderDTO.getParentFolderId() + " not found"
                    ));
            folder.setParentFolder(parent);
        }

        logger.info("Saving folder '{}' for user '{}'", folder.getName(), username);
        return folderRepository.save(folder);
    }

    @Transactional
    public Folder updateFolder(Long id, FolderDTO folderDTO) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Folder with ID " + id + " not found"
                ));

        folder.setName(folderDTO.getName());

        if (folderDTO.getParentFolderId() != null) {
            Folder parent = folderRepository.findById(folderDTO.getParentFolderId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Parent folder with ID " + folderDTO.getParentFolderId() + " not found"
                    ));

            // Заборона зробити папку своїм же батьком
            if (parent.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder cannot be its own parent");
            }

            folder.setParentFolder(parent);
        } else {
            folder.setParentFolder(null);
        }

        logger.info("Updating folder with ID {} by '{}'", id, getCurrentUsername());
        return folderRepository.save(folder);
    }

    @Transactional
    public void deleteFolder(Long id) {
        if (folderRepository.existsById(id)) {
            logger.info("Deleting folder with ID: {}", id);
            folderRepository.deleteById(id);
        } else {
            logger.warn("Folder with ID: {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder with ID " + id + " not found");
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with username '" + username + "' not found"
                ));
    }
}

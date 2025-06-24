package com.filemanager.backend.controller;

import com.filemanager.backend.dto.FileResponseDTO;
import com.filemanager.backend.dto.FolderResponseDTO;
import com.filemanager.backend.dto.UserDTO;
import com.filemanager.backend.mapper.FileMapper;
import com.filemanager.backend.mapper.FolderMapper;
import com.filemanager.backend.service.FileService;
import com.filemanager.backend.service.FolderService;
import com.filemanager.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Access restricted to ADMIN only
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final FileService fileService;
    private final FolderService folderService;
    private final FileMapper fileMapper;
    private final FolderMapper folderMapper;

    public AdminController(UserService userService,
                           FileService fileService,
                           FolderService folderService,
                           FileMapper fileMapper,
                           FolderMapper folderMapper) {
        this.userService = userService;
        this.fileService = fileService;
        this.folderService = folderService;
        this.fileMapper = fileMapper;
        this.folderMapper = folderMapper;
    }

    // 🔹 Admin dashboard ping
    @GetMapping("/dashboard")
    public String adminDashboard() {
        logger.info("Admin dashboard accessed");
        return "Welcome to Admin Dashboard!";
    }

    // 🔹 Get all users
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 🔹 Delete user by ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Get all files (DTO)
    @GetMapping("/files")
    public ResponseEntity<List<FileResponseDTO>> getAllFiles() {
        return ResponseEntity.ok(
                fileService.getAllFiles().stream()
                        .map(fileMapper::toDTO)
                        .toList()
        );
    }

    // 🔹 Delete file by ID
    @DeleteMapping("/files/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Get all folders (DTO)
    @GetMapping("/folders")
    public ResponseEntity<List<FolderResponseDTO>> getAllFolders() {
        return ResponseEntity.ok(
                folderService.getAllFolders().stream()
                        .map(folderMapper::toDTO)
                        .toList()
        );
    }

    // 🔹 Delete folder by ID
    @DeleteMapping("/folders/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
        return ResponseEntity.noContent().build();
    }
}

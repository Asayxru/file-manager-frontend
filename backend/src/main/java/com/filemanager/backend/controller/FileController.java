package com.filemanager.backend.controller;

import com.filemanager.backend.dto.FileDTO;
import com.filemanager.backend.dto.FileResponseDTO;
import com.filemanager.backend.entity.File;
import com.filemanager.backend.entity.Folder;
import com.filemanager.backend.entity.User;
import com.filemanager.backend.mapper.FileMapper;
import com.filemanager.backend.service.FileService;
import com.filemanager.backend.service.FileStorageService;
import com.filemanager.backend.service.FolderService;
import com.filemanager.backend.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;
    private final FileStorageService fileStorageService;
    private final UserService userService;
    private final FolderService folderService;
    private final FileMapper fileMapper;

    public FileController(FileService fileService,
                          FileStorageService fileStorageService,
                          UserService userService,
                          FolderService folderService,
                          FileMapper fileMapper) {
        this.fileService = fileService;
        this.fileStorageService = fileStorageService;
        this.userService = userService;
        this.folderService = folderService;
        this.fileMapper = fileMapper;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<FileResponseDTO> getFileById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        return fileService.getFileById(id)
                .filter(file -> isAdmin || file.getOwner().getUsername().equals(username))
                .map(fileMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<FileResponseDTO>> getAllFiles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        List<File> files = isAdmin
                ? fileService.getAllFiles()
                : fileService.getFilesByOwner(username);

        List<FileResponseDTO> dtoList = files.stream()
                .map(fileMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<FileResponseDTO>> searchFiles(@RequestParam("query") String query) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        List<File> files = isAdmin
                ? fileService.searchFilesByName(query)
                : fileService.searchFilesByNameAndOwner(query, username);

        List<FileResponseDTO> dtoList = files.stream()
                .map(fileMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FileResponseDTO> saveFile(@RequestBody @Valid FileDTO fileDTO) {
        File file = new File();
        file.setName(fileDTO.getName());
        file.setPath(fileDTO.getPath());
        file.setSize(fileDTO.getSize());
        file.setVisibility(File.Visibility.valueOf(fileDTO.getVisibility().toUpperCase()));

        User owner = userService.getUserEntityById(fileDTO.getOwnerId());
        file.setOwner(owner);

        if (fileDTO.getFolderId() != null) {
            Folder folder = folderService.getFolderById(fileDTO.getFolderId())
                    .orElseThrow(() -> new RuntimeException("Folder not found"));
            file.setFolder(folder);
        }

        return ResponseEntity.ok(fileMapper.toDTO(fileService.saveFile(file)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<FileResponseDTO> updateFile(@PathVariable Long id, @RequestBody FileDTO fileDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        Optional<File> fileOpt = fileService.getFileById(id);
        if (fileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        File existingFile = fileOpt.get();

        if (!isAdmin && !existingFile.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(403).build();
        }

        if (fileDTO.getName() != null) existingFile.setName(fileDTO.getName());
        if (fileDTO.getPath() != null) existingFile.setPath(fileDTO.getPath());
        if (fileDTO.getSize() != null) existingFile.setSize(fileDTO.getSize());
        if (fileDTO.getVisibility() != null)
            existingFile.setVisibility(File.Visibility.valueOf(fileDTO.getVisibility().toUpperCase()));
        if (fileDTO.getOwnerId() != null) {
            User owner = userService.getUserEntityById(fileDTO.getOwnerId());
            existingFile.setOwner(owner);
        }

        if (fileDTO.getFolderId() != null) {
            Folder folder = folderService.getFolderById(fileDTO.getFolderId())
                    .orElseThrow(() -> new RuntimeException("Folder not found"));
            existingFile.setFolder(folder);
        } else {
            existingFile.setFolder(null);
        }

        File updated = fileService.updateFile(id, existingFile);
        return ResponseEntity.ok(fileMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        File file = fileService.getFileById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!isAdmin && !file.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(403).build();
        }

        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<FileResponseDTO> uploadFile(
            @Parameter(description = "Файл для завантаження", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderId", required = false) Long folderId
    ) {
        try {
            File fileEntity = fileStorageService.store(file);
            User owner = userService.getCurrentAuthenticatedUser();
            fileEntity.setOwner(owner);

            if (folderId != null) {
                Folder folder = folderService.getFolderById(folderId)
                        .orElseThrow(() -> new RuntimeException("Folder not found"));
                fileEntity.setFolder(folder);
            }

            File saved = fileService.saveFile(fileEntity);
            return ResponseEntity.ok(fileMapper.toDTO(saved));
        } catch (IOException e) {
            logger.error("Помилка при завантаженні файлу", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/download/{filename}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            logger.error("Помилка при скачуванні файлу", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/preview/by-id/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> previewFileById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        File file = fileService.getFileById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!isAdmin && !file.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(403).body("Access denied");
        }

        try {
            String content = fileStorageService.readAsText(file.getName());
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            logger.error("Помилка при читанні файлу: {}", file.getName(), e);
            return ResponseEntity.internalServerError().body("Помилка при читанні файлу: " + e.getMessage());
        }
    }
}

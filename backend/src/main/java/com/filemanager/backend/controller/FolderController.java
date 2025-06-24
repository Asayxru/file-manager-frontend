package com.filemanager.backend.controller;

import com.filemanager.backend.dto.FolderDTO;
import com.filemanager.backend.dto.FolderResponseDTO;
import com.filemanager.backend.entity.Folder;
import com.filemanager.backend.entity.User;
import com.filemanager.backend.mapper.FolderMapper;
import com.filemanager.backend.service.FolderService;
import com.filemanager.backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private static final Logger logger = LoggerFactory.getLogger(FolderController.class);

    private final FolderService folderService;
    private final FolderMapper folderMapper;
    private final UserService userService;

    public FolderController(FolderService folderService, FolderMapper folderMapper, UserService userService) {
        this.folderService = folderService;
        this.folderMapper = folderMapper;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<FolderResponseDTO>> getAllFolders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        List<Folder> folders = isAdmin
                ? folderService.getAllFolders()
                : folderService.getFoldersByOwner(username);

        List<FolderResponseDTO> responseDTOs = folders.stream()
                .map(folderMapper::toDTOWithFiles)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<FolderResponseDTO> getFolderById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        return folderService.getFolderById(id)
                .filter(folder -> isAdmin || folder.getOwner().getUsername().equals(username))
                .map(folderMapper::toDTOWithFiles)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<FolderResponseDTO> saveFolder(@RequestBody @Valid FolderDTO folderDTO) {
        User owner = userService.getCurrentAuthenticatedUser();
        folderDTO.setOwnerId(owner.getId());

        Folder savedFolder = folderService.saveFolder(folderDTO);
        return ResponseEntity.ok(folderMapper.toDTOWithFiles(savedFolder));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<FolderResponseDTO> updateFolder(@PathVariable Long id, @RequestBody @Valid FolderDTO folderDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        Folder folder = folderService.getFolderById(id)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (!isAdmin && !folder.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(403).build();
        }

        Folder updatedFolder = folderService.updateFolder(id, folderDTO);
        return ResponseEntity.ok(folderMapper.toDTOWithFiles(updatedFolder));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        Folder folder = folderService.getFolderById(id)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (!isAdmin && !folder.getOwner().getUsername().equals(username)) {
            return ResponseEntity.status(403).build();
        }

        folderService.deleteFolder(id);
        return ResponseEntity.noContent().build();
    }
}

package com.filemanager.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO used for safely returning file metadata to clients.
 * Hides internal entities (like User or Folder) behind simple fields.
 */
@Data
public class FileResponseDTO {
    private Long id;
    private String name;
    private String path;
    private Long size;
    private String visibility;
    private String ownerUsername;
    private Long folderId;
    private String folderName; // нове поле
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

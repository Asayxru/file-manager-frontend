package com.filemanager.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO used for returning folder metadata safely to the client.
 */
@Data
public class FolderResponseDTO {
    private Long id;
    private String name;
    private Long ownerId;
    private Long parentFolderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<FileResponseDTO> files;              
    private List<FolderResponseDTO> subfolders;       
}

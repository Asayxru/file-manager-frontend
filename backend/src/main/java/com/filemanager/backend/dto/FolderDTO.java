package com.filemanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating or updating folders.
 * Supports optional parent folder for nested structure.
 */
@Data
public class FolderDTO {

    @NotBlank(message = "Folder name must not be blank")
    private String name;

    // Optional: ID of parent folder for nesting
    private Long parentFolderId;

    private Long ownerId;
}

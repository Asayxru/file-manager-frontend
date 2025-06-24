package com.filemanager.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for creating or updating files.
 * Used by admin or internal logic.
 */
@Data
public class FileDTO {

    // Name is required only for creating new files
    private String name;

    private String path;

    @Min(value = 0, message = "File size must be non-negative")
    private Long size;

    private String visibility;

    // Optional for update, required for create
    private Long ownerId;

    // Optional: assign or move file to a folder
    private Long folderId;
}
